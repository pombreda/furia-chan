package org.kit.furia.fragment.soot;

import java.util.List;
import org.apache.log4j.Logger;
import org.kit.furia.fragment.soot.representation.Frimp;
import org.kit.furia.fragment.soot.representation.internal.FExprBox;
import org.kit.furia.fragment.soot.representation.internal.FPhiExpr;
import org.kit.furia.fragment.soot.representation.internal.FSelfReference;
import org.kit.furia.misc.IntegerHolder;



import soot.Body;
import soot.Unit;
import soot.Value;
import soot.shimple.PhiExpr;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;

import soot.ValueBox;

import java.util.Map;
import java.util.HashMap;

import soot.grimp.Grimp;
import soot.grimp.internal.ExprBox;
import soot.jimple.ArrayRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.AbstractDefinitionStmt;
import soot.jimple.internal.JimpleLocal;

import java.util.LinkedList;
import java.util.Stack;
import java.util.Iterator;

/**
 * Hay dos cosas que se pueden hacer: 1) micro slices: cuando encuentro un phi,
 * termino el slice, borro el phi, y creo n nuevos slices con los n elementos
 * del phi 2) exp slices: cuando encuentro un phi, creo n nuevos slices con los
 * n elementos del phi borrando el phi la cosa es crear un nuevo slice por
 * branch, muchos nuevos slices seran creados eso si...
 * @author amuller
 */
public class FragmentBuilder {

    private static final Logger logger = Logger
            .getLogger(FragmentBuilder.class);

    private int hugeSlices;

    private int smallSlices;

    private final Map < Value, ValueBox > slices;

    //private final Map < Value, Integer > sliceSizes;

    private final String methodName;

    public FragmentBuilder(final Body body, final BlockGraph graph,
            int max_allowed_size, int min_allowed_size) {
        hugeSlices = 0;
        smallSlices = 0;
        //sliceSizes = new HashMap < Value, Integer >();
        methodName = body.getMethod().getSignature();
        Map < Value, ValueBox > unexpandedSlices = extractUnexpandedSlices(graph);
        slices = expandSlices(unexpandedSlices, max_allowed_size,
                min_allowed_size);

    }

    public Map < Value, ValueBox > getSlices() {
        return slices;
    }

    /*public int getSliceSize(Value x) {
        return sliceSizes.get(x);
    }*/

    /*
     * Extracts all the slices from all the blocks of the method
     */
    private Map < Value, ValueBox > extractUnexpandedSlices(
            final BlockGraph graph) {

        final Map < Value, ValueBox > res = new HashMap();
        final Iterator it = graph.getBlocks().iterator();
        while (it.hasNext()) {
            final Block block = (Block) it.next();
            final Iterator blockIt = block.iterator();
            while (blockIt.hasNext()) {
                final Unit ins = (Unit) blockIt.next();

                if (ins instanceof AbstractDefinitionStmt) {
                    final AbstractDefinitionStmt a = (AbstractDefinitionStmt) ins;
                    final ValueBox rightvb = new FExprBox(a.getRightOp());
                    Value left = a.getLeftOp();

                    // if this assert rings, you have to update expandSliceAux
                    // here: if(j.contains(v) || ! (v instanceof JimpleLocal))
                    assert left instanceof ArrayRef
                            || left instanceof JimpleLocal
                            || left instanceof InstanceFieldRef
                            || left instanceof StaticFieldRef : " left var instance of:"
                            + left.getClass().getName();

                    if (res.containsKey(left)) { // if we already have the
                        // variable, create a phi
                        // construct.
                        final ValueBox vb = res.get(left);
                        final Value v = vb.getValue();
                        if (v instanceof FPhiExpr) {
                            ((FPhiExpr) v).add(rightvb);
                        } else {
                            // if there is no phi expression we create one
                            final List<Value> x = new LinkedList<Value>();
                            x.add(v);
                            x.add(rightvb.getValue());
                            FPhiExpr n = new FPhiExpr(x);
                            res.put(left, new FExprBox(n));
                        }
                    } else {
                        res.put(left, rightvb);
                    }
                }
            }
        }
        return res;
    }

    public int getTotalSlices() {
        return this.slices.size();
    }

    /*
     * best expansion approach: expand fully, if the slice grows toooo much,
     * remove the slice Expands all the slices in a method
     */
    private Map < Value, ValueBox > expandSlices(
            final Map < Value, ValueBox > ue, final int max_allowed_size,
            final int min_allowed_size) {
        this.hugeSlices = 0;
        this.smallSlices = 0;
        Map < Value, ValueBox > res = new HashMap < Value, ValueBox >();
        Iterator < Value > it = ue.keySet().iterator();
        while (it.hasNext()) {
            Value k = it.next();
            Stack < Value > j = new Stack < Value >();
            j.push(k); // put our slice at the top of the stack
            IntegerHolder i = new IntegerHolder(1);
            try {
                // expand one slice
                Value expanded = expandSliceAux(j, ue, k, i, max_allowed_size);

                int size = i.getValue();
                if (size > max_allowed_size) {
                    throw new HugeFragmentException();
                } else if (size >= min_allowed_size) {
                    res.put(k, new FExprBox(expanded));
                    //sliceSizes.put(k, Integer.valueOf(size));
                } else {
                    this.smallSlices++;
                }
            } catch (HugeFragmentException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Huge slice of size: " + i.getValue()
                            + " in: " + k + " " + this.methodName);
                }
                hugeSlices++;
            }

        }
        return res;
    }

    private Value expandSliceAux(final Stack < Value > j,
            final Map < Value, ValueBox > ue, final Value originalValue,
            IntegerHolder i, int max_allowed_size) // NOPMD by amuller on
                                                    // 11/16/06 4:07 PM
            throws HugeFragmentException {
        if (i.getValue() > max_allowed_size) { // if the size is getting tooooo
                                                // big
            throw new HugeFragmentException();
        }
        // right hand side is backed up.
        Value rightToProcess;
        // no references in list of pairs key => value
        if (null == ue.get(j.peek())) {
            rightToProcess = Grimp.cloneIfNecessary(j.peek());
        } else {
            // we get the right hand side from our list of pairs key => value
            rightToProcess = Grimp.cloneIfNecessary(((ValueBox) ue
                    .get(j.peek())).getValue());
        }
        // we process what we have at the top of the stack.
        Iterator it = rightToProcess.getUseBoxes().iterator();
        while (it.hasNext()) {
            ValueBox vb = (ValueBox) it.next();
            Value v = vb.getValue();
            // if the value that we are evaluating is the "original value", we
            // replace it
            if (v.equals(originalValue)) {
                vb.setValue(new FSelfReference());
                continue;
            }
            // if the value has been expanded, we continue to the next value
            if (j.contains(v)) {
                continue;
            }
            // or if the value is not a reference we continue to the next value
            if (!(v instanceof JimpleLocal || v instanceof InstanceFieldRef || v instanceof StaticFieldRef)) {
                continue;
            }
            i.inc();
            j.push(v);
            
            vb.setValue(expandSliceAux(j, ue, originalValue, i,
                    max_allowed_size));
            
        }
        j.pop();
        return rightToProcess;
    }

    public int getHugeSlices() {
        return hugeSlices;
    }

    public String getMethodName() {
        return methodName;
    }

    /**
     * The number attached to the slice is the # of expansions performed on the
     * slice.
     */
    public void generateString(StringBuilder result) throws Exception {

        //result.append("// Method: " + getMethodName() + "\n");
        Iterator < Value > keys = slices.keySet().iterator();
        while (keys.hasNext()) {
            Value keyValue = keys.next();
            Value value = slices.get(keyValue).getValue();
            result.append(Frimp.toQ(value));
            result.append("\n");
        }
    }

    /**
    
    public String generateStringSpecial(boolean debugSlices) throws Exception {
        StringBuilder result = new StringBuilder();
        // if(debugSlices){
        result.append("// Method: " + getMethodName() + "\n");
        // }
        Iterator < Value > keys = slices.keySet().iterator();
        Stack < SliceAST > stack = new Stack < SliceAST >();
        while (keys.hasNext()) {
            Value keyValue = keys.next();
            Value value = slices.get(keyValue).getValue();
            String slice = Frimp.toQ(value);
            SliceAST s = SliceFactory.createSliceASTLean(slice);
            s.getSize();
            result.append(microSliceString(s));
        }
        return result.toString();
    }

    

    public static void doMicroSlice(SliceAST s, LinkedList < SliceAST > result) {
        if (!isPhi(s)) {
            result.add(s);
            microSliceAux(s, result);
        } else {
            splitPhi(s, result);
        }
    }

    protected static void microSliceAux(SliceAST s, LinkedList < SliceAST > res) {
        SliceAST down = (SliceAST) s.getFirstChild();
        boolean hadPhi = false;
        if (down != null) {
            if (isPhi(down)) {
                // 1) remove it
                s.setFirstChild(down.getNextSibling());
                // 2) create new slices out of down
                splitPhi(down, res);
                hadPhi = true;
            } else {
                microSliceAux(down, res);
            }
        }
        SliceAST right = (SliceAST) s.getNextSibling();
        if (right != null) {
            if (isPhi(right)) {
                // 1) remove it
                s.setNextSibling(right.getNextSibling());
                // 2) create new slices out of right
                splitPhi(right, res);
                hadPhi = true;
            } else {
                microSliceAux(right, res);
            }
        }
        if (hadPhi) {
            microSliceAux(s, res); // need to execute again
        }
    }

    protected static void splitPhi(SliceAST s, LinkedList < SliceAST > res) {
        SliceAST t = s.getLeftmostChild();
        assert isPhi(s);
        while (t != null) {
            SliceAST a = t;
            t = (SliceAST) t.getNextSibling();
            a.setNextSibling(null); // we have to create a new slice
            doMicroSlice(a, res); // creates new slices
        }
    }

    protected static boolean isPhi(SliceAST s) {
        return s.getText().equals(FuriaConstructDefinitions.FURIA_fphi);
    }

    public static String microSliceString(SliceAST s) {
        LinkedList < SliceAST > result = new LinkedList < SliceAST >();
        doMicroSlice(s, result);
        Iterator < SliceAST > it = result.iterator();
        StringBuilder res = new StringBuilder();
        while (it.hasNext()) {
            SliceAST t = it.next();
            res.append(t.toQ());
            res.append("\n");
        }
        String r = res.toString();
        assert !r.contains("p");
        return r;
    }*/

    

    public int getSmallSlices() {
        return smallSlices;
    }

}


