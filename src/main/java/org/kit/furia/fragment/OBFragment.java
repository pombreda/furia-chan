package org.kit.furia.fragment;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.ajmm.obsearch.exception.OBException;
import org.ajmm.obsearch.ob.OBShort;
import org.kit.furia.misc.IntegerHolder;

import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/*
 Furia-chan: An Open Source software license violation detector.    
 Copyright (C) 2008 Kyushu Institute of Technology

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Implementation of the mtd algorithm. This algorithm exploits hash codes from
 * string representation of the tree and its complete subtrees. Additionally, it
 * exploits the fact that once two complete subtrees j and m that belong
 * respectively to trees T1 and T2, the intersection of j and m and all their
 * complete subtrees can be calculated in linear time. This requires to have a
 * value of multiplicity in each node of T1 and T2.
 * So the expected running time is around O(|T1|) (where |T1| the size of the smallest tree)
 * @author Arnoldo Jose Muller Molina
 */

public final class OBFragment implements OBShort {

    private Map < MTDFragmentAST, Tuple > mapS;

    private Map < String, IntegerHolder > mapN;

    private int maxId;

    private MTDFragmentAST tree;
    
    public OBFragment(){
        
    }

    public OBFragment(String x) throws OBException {
        updateTree(x);
    }

    /**
     * Internal method that updates the Tree from the String
     * @throws OBException
     */
    protected final void updateTree(String x) throws OBException {
        tree = parseTree(x);

        mapS = new HashMap < MTDFragmentAST, Tuple >();
        mapN = new HashMap < String, IntegerHolder >();
        decorate(tree, new IntegerHolder(0));
    }

    public static final  MTDFragmentAST parseTree(String x)
            throws FragmentParseException {
        try {
            FragmentLexer lexer = new FragmentLexer(new StringReader(x));
            FragmentParser parser = new FragmentParser(lexer);
            parser.setASTNodeClass("org.kit.furia.fragment.MTDFragmentAST");
            parser.fragment();
            MTDFragmentAST t = (MTDFragmentAST) parser.getAST();
            t.update();
            return t;
        } catch (Exception e) {
            throw new FragmentParseException(x, e);
        }
    }

    /**
     * Returns the size (in nodes) of the tree.
     * @return The size of the tree.
     * @throws OBException
     *                 If something goes wrong.
     */
    public final int size() throws OBException {
        return tree.getSize();
    }

    /**
     * @return A String representation of the tree.
     */
    public final String toString() {
        String res = ":(";
        try {
            res = tree.toFuriaChanTree() + "|" + tree.getSize() + "|";
        } catch (Exception e) {
            assert false;
        }
        return res;
    }

    /**
     * Re-creates this object from the given byte stream
     * @param in
     *                A byte stream with the data that must be loaded.
     * @see org.ajmm.obsearch.Storable#load(com.sleepycat.bind.tuple.TupleInput)
     */
    public final void load(TupleInput in) throws OBException {
        short size = in.readShort();
        updateTree(in.readBytes(size));
    }

    /**
     * Stores this object into the given byte stream.
     * @param out
     *                The byte stream to be used
     * @see org.ajmm.obsearch.Storable#store(com.sleepycat.bind.tuple.TupleOutput)
     */
    public final void store(TupleOutput out) {
        String str = tree.toFuriaChanTree();
        out.writeShort(str.length());
        out.writeBytes(str);
    }

    private void decorate(MTDFragmentAST t, IntegerHolder id) {
        if (t == null) {
            return;
        }
        t.update();
        Tuple tMapped = mapS.get(t);
        if (tMapped != null) {
            assert tMapped.tree.hashCode() == t.hashCode();
            tMapped.repetitions.inc();

        } else {
            tMapped = new Tuple();
            tMapped.repetitions = new IntegerHolder(1);
            tMapped.id = id.getValue();
            tMapped.tree = t;
            mapS.put(t, tMapped);
            id.inc();
            maxId = id.getValue();
        }
        t.id = tMapped.id;
        t.repetitions = tMapped.repetitions;
        assert mapS.get(t).tree.equals(t) : mapS.get(t).tree.prettyPrint()
                + " != " + t.prettyPrint();
        assert t.equals(tMapped.tree);
        IntegerHolder node = mapN.get(t.getText());
        if (node != null) {
            node.inc();
        } else {
            mapN.put(t.getText(), new IntegerHolder(1));
        }
        decorate(t.getLeft(), id);
        decorate(t.getSibbling(), id);
    }

    public final short distance(OBShort other) {
        OBFragment b = (OBFragment) other;
        // use the smallest tree for the match!
        if (this.tree.getSize() < b.tree.getSize()) {
            return this.mtd( b);
        } else {
            return b.mtd( this);
        }

    }

    public final short mtd(OBFragment other) {

        int res =  (ds(other) + dn(other) ) / 2;
        //assert res == distance(this.tree, other.tree);
        return (short)res;
    }

    private int dnAux(OBFragment other) {
        int res = 0;
        Iterator < Map.Entry < String, IntegerHolder >> keysIt = mapN
                .entrySet().iterator();
        while (keysIt.hasNext()) {
            Map.Entry < String, IntegerHolder > entry = keysIt.next();
            IntegerHolder n2 = other.mapN.get(entry.getKey());
            if (n2 != null) {
                IntegerHolder n1 = entry.getValue();
                res += Math.min(n1.getValue(), n2.getValue());
            }
        }
        return res;
    }

    public int ds(OBFragment other) {
        boolean[] visited = new boolean[maxId];
        int res = ds(tree, other, visited);
        return (tree.getSize() + other.tree.getSize()) - (2 * res);
    }

    public int dn(OBFragment other) {
        int res = dnAux(other);
        return (tree.getSize() + other.tree.getSize()) - (2 * res);
    }

    /*
     * public int ftedAux(MTDFragmentASTHolder other) { boolean[] visited = new
     * boolean[maxId]; int res = ds(tree, other, visited); return
     * (tree.getSize() + other.tree.getSize()) - (2 * res); }
     */

    private int ds(MTDFragmentAST current, OBFragment other, boolean[] visited) {
        if (current == null) {
            return 0;
        }
        int res = 0;
        if (!visited[current.id]) {

            Tuple m2 = other.mapS.get(current);

            if (m2 != null) {
                assert current.equals(m2.tree) : " Tree A:"
                        + current.prettyPrint() + " Tree B: "
                        + m2.tree.prettyPrint() + " hash A "
                        + current.hashCode() + " hash B " + m2.tree.hashCode();
                res = update(current, m2.tree, visited);
            } else {
                res = ds(current.getLeft(), other, visited);
            }
        }
        return res + ds(current.getSibbling(), other, visited);
    }

    private int update(MTDFragmentAST current, MTDFragmentAST current2,
            boolean[] visited) {
        assert current != null;
        if (visited[current.id]) {
            return 0;
        }
        visited[current.id] = true;
        int res = Math.min(current.repetitions.getValue(), current2.repetitions
                .getValue());
        return res + updateAux(current.getLeft(), current2.getLeft(), visited);
    }

    private int updateAux(MTDFragmentAST current, MTDFragmentAST current2,
            boolean[] visited) {
        if (current == null) {
            return 0;
        }
        assert current != null && current2 != null;
        int res = 0, res1 = 0, res2 = 0;

        if (!visited[current.id]) {

            visited[current.id] = true;
            res = Math.min(current.repetitions.getValue(), current2.repetitions
                    .getValue());
            res1 = updateAux(current.getLeft(), current2.getLeft(), visited);

        }
        res2 = updateAux(current.getSibbling(), current2.getSibbling(), visited);
        return res + res1 + res2;

    }

    private class Tuple {
        public MTDFragmentAST tree;

        public int id;

        public IntegerHolder repetitions;

        public String toString() {
            return tree.toString() + " " + id + " " + repetitions.getValue();
        }
    }

    public MTDFragmentAST getTree() {
        return tree;
    }

    public void setTree(MTDFragmentAST tree) {
        this.tree = tree;
    }

    /**
     * Returns true of this.tree.equals(obj.tree). For this distance function
     * this.distance(obj) == 0 implies that this.equals(obj) == true
     * @param obj
     *                Object to compare.
     * @return true if this == obj
     */
    public final boolean equals(final Object obj) {
        return tree.equals(obj);
    }

    /**
     * A hashCode based on the string representation of the tree.
     * @return a hash code of the string representation of this tree.
     */
    public final int hashCode() {
        return this.tree.hashCode();
    }
    
    /**
     * Calculates the distance between trees a and b. Used to validate the
     * distance implementation. Only to be executed in assert mode.
     * @param a
     *                The first tree (should be smaller than b)
     * @param b
     *                The second tree
     * @return The distance of the trees.
     */
    private final short distance(FragmentAST a, FragmentAST b) {

        List < FragmentAST > aExpanded = a.depthFirst();
        List < FragmentAST > bExpanded = b.depthFirst();
        List < FragmentAST > bExpanded2 = new LinkedList < FragmentAST >();
        bExpanded2.addAll(bExpanded);
        int Na = aExpanded.size() * 2;
        int Nb = bExpanded.size() * 2;

        ListIterator < FragmentAST > ait = aExpanded.listIterator();
        int res = 0;
        while (ait.hasNext()) {
            FragmentAST aTree = ait.next();
            ListIterator < FragmentAST > bit = bExpanded.listIterator();
            while (bit.hasNext()) {
                FragmentAST bTree = bit.next();
                if (aTree.equalsTree(bTree)) {
                    res++;
                    bit.remove();
                    break;
                }
            }
            // do the same for the nodes without children
            bit = bExpanded2.listIterator();
            while (bit.hasNext()) {
                FragmentAST bTree = bit.next();
                if (aTree.getText().equals(bTree.getText())) {
                    res++;
                    bit.remove();
                    break;
                }
            }
        }
        // return Na - res + Nb - res;
        // return (Na + Nb) - ( 2 * res);
        short r = (short) (((Na + Nb) - (2 * res)) / 2);
        assert r >= 0;
        return r;
    }

}
