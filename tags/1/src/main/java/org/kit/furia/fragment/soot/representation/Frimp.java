/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */






package org.kit.furia.fragment.soot.representation;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JCaughtExceptionRef;
import soot.jimple.internal.JNewExpr;

import org.kit.furia.fragment.soot.representation.internal.*;

import soot.shimple.PhiExpr;
import soot.shimple.internal.SPhiExpr;
import soot.util.*;

import java.util.*;
import org.apache.log4j.Logger;

/**
    The Grimp class contains all the constructors for the components of the Grimp
    grammar for the Grimp body. <br><br>

    Immediate -> Local | Constant <br>
    RValue -> Local | Constant | ConcreteRef | Expr<br>
    Variable -> Local | ArrayRef | InstanceFieldRef | StaticFieldRef <br>
 */


public class Frimp
{
	
	private final static Logger logger = Logger.getLogger(Frimp.class);
	
	private static Frimp v = new Frimp();
	
    public Frimp( Singletons.Global g ) {}
    
    public Frimp(){}
    // TODO check if we really need this (compliance to style). I don't think so
    public static Frimp v() { return v; }

    /**
        Constructs a XorExpr(Expr, Expr) grammar chunk.
     */

    public XorExpr newXorExpr(Value op1, Value op2)
    {
        return new FXorExpr(op1, op2);
    }


    /**
        Constructs a UshrExpr(Expr, Expr) grammar chunk.
     */

    public UshrExpr newUshrExpr(Value op1, Value op2)
    {
        return new FUshrExpr(op1, op2);
    }


    /**
        Constructs a SubExpr(Expr, Expr) grammar chunk.
     */

    public SubExpr newSubExpr(Value op1, Value op2)
    {
        return new FSubExpr(op1, op2);
    }
    



    /**
        Constructs a ShrExpr(Expr, Expr) grammar chunk.
     */

    public ShrExpr newShrExpr(Value op1, Value op2)
    {
        return new FShrExpr(op1, op2);
    }


    /**
        Constructs a ShlExpr(Expr, Expr) grammar chunk.
     */

    public ShlExpr newShlExpr(Value op1, Value op2)
    {
        return new FShlExpr(op1, op2);
    }


    /**
        Constructs a RemExpr(Expr, Expr) grammar chunk.
     */

    public RemExpr newRemExpr(Value op1, Value op2)
    {
        return new FRemExpr(op1, op2);
    }


    /**
        Constructs a OrExpr(Expr, Expr) grammar chunk.
     */

    public OrExpr newOrExpr(Value op1, Value op2)
    {
        return new FOrExpr(op1, op2);
    }


    /**
        Constructs a NeExpr(Expr, Expr) grammar chunk.
     */

    public NeExpr newNeExpr(Value op1, Value op2)
    {
        return new FNeExpr(op1, op2);
    }


    /**
        Constructs a MulExpr(Expr, Expr) grammar chunk.
     */

    public MulExpr newMulExpr(Value op1, Value op2)
    {
        return new FMulExpr(op1, op2);
    }


    /**
        Constructs a LeExpr(Expr, Expr) grammar chunk.
     */

    public LeExpr newLeExpr(Value op1, Value op2)
    {
        return new FLeExpr(op1, op2);
    }


    /**
        Constructs a GeExpr(Expr, Expr) grammar chunk.
     */

    public GeExpr newGeExpr(Value op1, Value op2)
    {
        return new FGeExpr(op1, op2);
    }


    /**
        Constructs a EqExpr(Expr, Expr) grammar chunk.
     */

    public EqExpr newEqExpr(Value op1, Value op2)
    {
        return new FEqExpr(op1, op2);
    }

    /**
        Constructs a DivExpr(Expr, Expr) grammar chunk.
     */

    public DivExpr newDivExpr(Value op1, Value op2)
    {
        return new FDivExpr(op1, op2);
    }


    /**
        Constructs a CmplExpr(Expr, Expr) grammar chunk.
     */

    public CmplExpr newCmplExpr(Value op1, Value op2)
    {
        return new FCmplExpr(op1, op2);
    }


    /**
        Constructs a CmpgExpr(Expr, Expr) grammar chunk.
     */

    public CmpgExpr newCmpgExpr(Value op1, Value op2)
    {
        return new FCmpgExpr(op1, op2);
    }


    /**
        Constructs a CmpExpr(Expr, Expr) grammar chunk.
     */

    public CmpExpr newCmpExpr(Value op1, Value op2)
    {
        return new FCmpExpr(op1, op2);
    }


    /**
        Constructs a GtExpr(Expr, Expr) grammar chunk.
     */

    public GtExpr newGtExpr(Value op1, Value op2)
    {
        return new FGtExpr(op1, op2);
    }


    /**
        Constructs a LtExpr(Expr, Expr) grammar chunk.
     */

    public LtExpr newLtExpr(Value op1, Value op2)
    {
        return new FLtExpr(op1, op2);
    }

    /**
        Constructs a AddExpr(Expr, Expr) grammar chunk.
     */

    public AddExpr newAddExpr(Value op1, Value op2)
    {
        return new FAddExpr(op1, op2);
    }


    /**
        Constructs a AndExpr(Expr, Expr) grammar chunk.
     */

    public AndExpr newAndExpr(Value op1, Value op2)
    {
        return new FAndExpr(op1, op2);
    }


    /**
        Constructs a NegExpr(Expr, Expr) grammar chunk.
     */

    public NegExpr newNegExpr(Value op)
    {
        return new FNegExpr(op);
    }


    /**
        Constructs a LengthExpr(Expr) grammar chunk.
     */

    public LengthExpr newLengthExpr(Value op)
    {
        return new FLengthExpr(op);
    }


    /**
        Constructs a CastExpr(Expr, Type) grammar chunk.
     */

    public CastExpr newCastExpr(Value op1, Type t)
    {
        return new FCastExpr(op1, t);
    }

    /**
        Constructs a InstanceOfExpr(Expr, Type)
        grammar chunk.
     */

    public InstanceOfExpr newInstanceOfExpr(Value op1, Type t)
    {
        return new FInstanceOfExpr(op1, t);
    }


    /**
        Constructs a NewExpr(RefType) grammar chunk.
     */

    NewExpr newNewExpr(RefType type)
    {
        return Jimple.v().newNewExpr(type);
    }


    /**
        Constructs a NewArrayExpr(Type, Expr) grammar chunk.
     */

    public NewArrayExpr newNewArrayExpr(Type type, Value size)
    {
        return new FNewArrayExpr(type, size);
    }

    /**
        Constructs a NewMultiArrayExpr(ArrayType, List of Expr) grammar chunk.
     */

    public NewMultiArrayExpr newNewMultiArrayExpr(ArrayType type, List sizes)
    {
        return new FNewMultiArrayExpr(type, sizes);
    }

    /**
        Constructs a NewInvokeExpr(Local base, List of Expr) grammar chunk.
     */

    public NewInvokeExpr newNewInvokeExpr(RefType base, SootMethodRef method, List args)
    {
        return (NewInvokeExpr)new FNewInvokeExpr(base, method, args);
    }

    /**
        Constructs a StaticInvokeExpr(ArrayType, List of Expr) grammar chunk.
     */

    public StaticInvokeExpr newStaticInvokeExpr(SootMethodRef method, List args)
    {
        return new FStaticInvokeExpr(method, args);
    }


    /**
        Constructs a SpecialInvokeExpr(Local base, SootMethodRef method, List of Expr) grammar chunk.
     */

    public SpecialInvokeExpr newSpecialInvokeExpr(Local base, SootMethodRef method, List args)
    {
        return new FSpecialInvokeExpr(base, method, args);
    }


    /**
        Constructs a VirtualInvokeExpr(Local base, SootMethodRef method, List of Expr) grammar chunk.
     */

    public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethodRef method, List args)
    {
        return new FVirtualInvokeExpr(base, method, args);
    }


    /**
        Constructs a InterfaceInvokeExpr(Local base, SootMethodRef method, List of Expr) grammar chunk.
     */

    public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethodRef method, List args)
    {
        return new FInterfaceInvokeExpr(base, method, args);
    }


    /**
        Constructs a ThrowStmt(Expr) grammar chunk.
     */

    public ThrowStmt newThrowStmt(Value op)
    {
        return new FThrowStmt(op);
    }

    public ThrowStmt newThrowStmt(ThrowStmt s)
    {
        return new FThrowStmt(s.getOp());
    }

    /**
        Constructs a ExitMonitorStmt(Expr) grammar chunk
     */

    public ExitMonitorStmt newExitMonitorStmt(Value op)
    {
        return new FExitMonitorStmt(op);
    }

    public ExitMonitorStmt newExitMonitorStmt(ExitMonitorStmt s)
    {
        return new FExitMonitorStmt(s.getOp());
    }

    /**
        Constructs a EnterMonitorStmt(Expr) grammar chunk.
     */

    public EnterMonitorStmt newEnterMonitorStmt(Value op)
    {
        return new FEnterMonitorStmt(op);
    }

    public EnterMonitorStmt newEnterMonitorStmt(EnterMonitorStmt s)
    {
        return new FEnterMonitorStmt(s.getOp());
    }

    /**
        Constructs a BreakpointStmt() grammar chunk.
     */

    public BreakpointStmt newBreakpointStmt()
    {
        return Jimple.v().newBreakpointStmt();
    }
    
    public BreakpointStmt newBreakpointStmt(BreakpointStmt s)
    {
        return Jimple.v().newBreakpointStmt();
    }

    /**
        Constructs a GotoStmt(Stmt) grammar chunk.
     */

    public GotoStmt newGotoStmt(Unit target)
    {
        return Jimple.v().newGotoStmt(target);
    }

    public GotoStmt newGotoStmt(GotoStmt s)
    {
        return Jimple.v().newGotoStmt(s.getTarget());
    }

    /**
        Constructs a NopStmt() grammar chunk.
     */

    public NopStmt newNopStmt()
    {
        return Jimple.v().newNopStmt();
    }

    public NopStmt newNopStmt(NopStmt s)
    {
        return Jimple.v().newNopStmt();
    }

    /**
        Constructs a ReturnVoidStmt() grammar chunk.
     */

    public ReturnVoidStmt newReturnVoidStmt()
    {
        return Jimple.v().newReturnVoidStmt();
    }

    public ReturnVoidStmt newReturnVoidStmt(ReturnVoidStmt s)
    {
        return Jimple.v().newReturnVoidStmt();
    }

    /**
        Constructs a ReturnStmt(Expr) grammar chunk.
     */

    public ReturnStmt newReturnStmt(Value op)
    {
        return new FReturnStmt(op);
    }

    public ReturnStmt newReturnStmt(ReturnStmt s)
    {
        return new FReturnStmt(s.getOp());
    }

    /**
        Constructs a IfStmt(Condition, Stmt) grammar chunk.
     */

    public IfStmt newIfStmt(Value condition, Unit target)
    {
        return new FIfStmt(condition, target);
    }

    public IfStmt newIfStmt(IfStmt s)
    {
        return new FIfStmt(s.getCondition(), s.getTarget());
    }

    /**
        Constructs a IdentityStmt(Local, IdentityRef) grammar chunk.
     */

    public IdentityStmt newIdentityStmt(Value local, Value identityRef)
    {
        return new FIdentityStmt(local, identityRef);
    }

    public IdentityStmt newIdentityStmt(IdentityStmt s)
    {
        return new FIdentityStmt(s.getLeftOp(), s.getRightOp());
    }

    /**
        Constructs a AssignStmt(Variable, RValue) grammar chunk.
     */

    public AssignStmt newAssignStmt(Value variable, Value rvalue)
    {
        return new FAssignStmt(variable, rvalue);
    }

    public AssignStmt newAssignStmt(AssignStmt s)
    {
        return new FAssignStmt(s.getLeftOp(), s.getRightOp());
    }

    /**
        Constructs a InvokeStmt(InvokeExpr) grammar chunk.
     */

    public InvokeStmt newInvokeStmt(Value op)
    {
        return new FInvokeStmt(op);
    }

    public InvokeStmt newInvokeStmt(InvokeStmt s)
    {
        return new FInvokeStmt(s.getInvokeExpr());
    }

    /**
        Constructs a TableSwitchStmt(Expr, int, int, List of Unit, Stmt) grammar chunk.
     */

    public TableSwitchStmt newTableSwitchStmt(Value key, int lowIndex, int highIndex, List targets, Unit defaultTarget)
    {
        return new FTableSwitchStmt(key, lowIndex, highIndex, targets, defaultTarget);
    }

    public TableSwitchStmt newTableSwitchStmt(TableSwitchStmt s)
    {
        return new FTableSwitchStmt(s.getKey(), s.getLowIndex(), 
                                    s.getHighIndex(), s.getTargets(),
                                    s.getDefaultTarget());
    }

    /**
        Constructs a LookupSwitchStmt(Expr, List of Expr, List of Unit, Stmt) grammar chunk.
     */

    public LookupSwitchStmt newLookupSwitchStmt(Value key, List lookupValues, List targets, Unit defaultTarget)
    {
        return new FLookupSwitchStmt(key, lookupValues, targets, defaultTarget);
    }

    public LookupSwitchStmt newLookupSwitchStmt(LookupSwitchStmt s)
    {
        return new FLookupSwitchStmt(s.getKey(), s.getLookupValues(),
                                     s.getTargets(), s.getDefaultTarget());
    }

    /**
        Constructs a Local with the given name and type.
    */

    public Local newLocal(String name, Type t)
    {
        return Jimple.v().newLocal(name, t);
    }

    /**
        Constructs a new Trap for the given exception on the given Stmt range with the given Stmt handler.
    */

    public Trap newTrap(SootClass exception, Unit beginStmt, Unit endStmt, Unit handlerStmt)
    {
        return new FTrap(exception, beginStmt, endStmt, handlerStmt);
    }

    public Trap newTrap(Trap trap)
    {
        return new FTrap(trap.getException(), trap.getBeginUnit(),
                         trap.getEndUnit(), trap.getHandlerUnit());
    }

    /**
        Constructs a StaticFieldRef(SootFieldRef) grammar chunk.
     */

    public StaticFieldRef newStaticFieldRef(SootFieldRef f)
    {
        return Jimple.v().newStaticFieldRef(f);
    }


    /**
        Constructs a ThisRef(RefType) grammar chunk.
     */

    public ThisRef newThisRef(RefType t)
    {
        return Jimple.v().newThisRef(t);
    }


    /**
        Constructs a ParameterRef(SootMethod, int) grammar chunk.
     */

    public ParameterRef newParameterRef(Type paramType, int number)
    {
        return Jimple.v().newParameterRef(paramType, number);
    }

    /**
        Constructs a InstanceFieldRef(Value, SootFieldRef) grammar chunk.
     */

    public InstanceFieldRef newInstanceFieldRef(Value base, SootFieldRef f)
    {
        return new FInstanceFieldRef(base, f);
    }


    /**
        Constructs a CaughtExceptionRef() grammar chunk.
     */

    public CaughtExceptionRef newCaughtExceptionRef()
    {
        return Jimple.v().newCaughtExceptionRef();
    }


    /**
        Constructs a ArrayRef(Local, Expr) grammar chunk.
     */

    public ArrayRef newArrayRef(Value base, Value index)
    {
        return new FArrayRef(base, index);
    }

    public ValueBox newVariableBox(Value value)
    {
        return Jimple.v().newVariableBox(value);
    }

    public ValueBox newLocalBox(Value value)
    {
        return Jimple.v().newLocalBox(value);
    }

    public ValueBox newRValueBox(Value value)
    {
        return new FRValueBox(value);
    }

    public ValueBox newImmediateBox(Value value)
    {
        return Jimple.v().newImmediateBox(value);
    }

    public ValueBox newExprBox(Value value)
    {
        return new FExprBox(value);
    }

    public ValueBox newArgBox(Value value)
    {
        return new FExprBox(value);
    }

    public ValueBox newObjExprBox(Value value)
    {
        return new FObjExprBox(value);
    }

    public ValueBox newIdentityRefBox(Value value)
    {
        return Jimple.v().newIdentityRefBox(value);
    }

    public ValueBox newConditionExprBox(Value value)
    {
        return Jimple.v().newConditionExprBox(value);
    }

    public ValueBox newInvokeExprBox(Value value)
    {
        return Jimple.v().newInvokeExprBox(value);
    }

    public UnitBox newStmtBox(Unit unit)
    {
        return Jimple.v().newStmtBox((Stmt) unit);
    }

    
    
    /**
     * Create a PhiExpr with the provided list of Values (Locals or
     * Constants) and the corresponding control flow predecessor
     * Blocks.  Instead of a list of predecessor blocks, you may
     * provide a list of the tail Units from the corresponding blocks.
     * an FPhiExpr has conditions related to each 
     **/
    public PhiExpr newPhiExpr(List args, List preds)
    {
        return new FPhiExpr(args, preds);
    }
    
    /**
     * Receives a list of value boxes and returns a list
     * of values
     * @param valueBoxes
     * @return
     */
    public static List valueBoxToValue(List valueBoxes){
    	Iterator it = valueBoxes.iterator();
    	Vector res = new Vector();
    	while(it.hasNext()){
    		ValueBox vb = (ValueBox) it.next();
    		res.addElement(vb.getValue());
    	}
    	return res;
    }
    
    /** Carries out the mapping from other Value's to Grimp Value's */
    public Value newExpr(Value value)
    {
    	if (value instanceof SPhiExpr){
    		final FExprBox returnedExpr = new FExprBox(IntConstant.v(0));
    		returnedExpr.setValue
            (newPhiExpr( valueBoxToValue( ((SPhiExpr)value).getArgs()) ,
            		((SPhiExpr)value).getPreds()));
    		return returnedExpr.getValue();
    	}else
        if (value instanceof Expr)
            {
                final FExprBox returnedExpr = new FExprBox(IntConstant.v(0));
                ((Expr)value).apply(new AbstractExprSwitch()
                {
                	
                	
                    public void caseAddExpr(AddExpr v)
                    {
                        returnedExpr.setValue
                            (newAddExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseAndExpr(AndExpr v)
                    {
                        returnedExpr.setValue
                            (newAndExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseCmpExpr(CmpExpr v)
                    {
                        returnedExpr.setValue
                            (newCmpExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseCmpgExpr(CmpgExpr v)
                    {
                        returnedExpr.setValue
                            (newCmpgExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseCmplExpr(CmplExpr v)
                    {
                        returnedExpr.setValue
                            (newCmplExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseDivExpr(DivExpr v)
                    {
                        returnedExpr.setValue
                            (newDivExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseEqExpr(EqExpr v)
                    {
                        returnedExpr.setValue
                            (newEqExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseNeExpr(NeExpr v)
                    {
                        returnedExpr.setValue
                            (newNeExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseGeExpr(GeExpr v)
                    {
                        returnedExpr.setValue
                            (newGeExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseGtExpr(GtExpr v)
                    {
                        returnedExpr.setValue
                            (newGtExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseLeExpr(LeExpr v)
                    {
                        returnedExpr.setValue
                            (newLeExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseLtExpr(LtExpr v)
                    {
                        returnedExpr.setValue
                            (newLtExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseMulExpr(MulExpr v)
                    {
                        returnedExpr.setValue
                            (newMulExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseOrExpr(OrExpr v)
                    {
                        returnedExpr.setValue
                            (newOrExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseRemExpr(RemExpr v)
                    {
                        returnedExpr.setValue
                            (newRemExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseShlExpr(ShlExpr v)
                    {
                        returnedExpr.setValue
                            (newShlExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseShrExpr(ShrExpr v)
                    {
                        returnedExpr.setValue
                            (newShrExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseUshrExpr(UshrExpr v)
                    {
                        returnedExpr.setValue
                            (newUshrExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseSubExpr(SubExpr v)
                    {
                        returnedExpr.setValue
                            (newSubExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseXorExpr(XorExpr v)
                    {
                        returnedExpr.setValue
                            (newXorExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v)
                    {
                        ArrayList newArgList = new ArrayList();
                        for (int i = 0; i < v.getArgCount(); i++)
                            newArgList.add(newExpr(v.getArg(i)));
                        returnedExpr.setValue
                            (newInterfaceInvokeExpr((Local)(v.getBase()),
                                                    v.getMethodRef(),
                                                    newArgList));
                    }

                    public void caseSpecialInvokeExpr(SpecialInvokeExpr v)
                    {
                        ArrayList newArgList = new ArrayList();
                        for (int i = 0; i < v.getArgCount(); i++)
                            newArgList.add(newExpr(v.getArg(i)));
                        returnedExpr.setValue
                            (newSpecialInvokeExpr((Local)(v.getBase()),
                                                    v.getMethodRef(),
                                                    newArgList));
                    }

                    public void caseStaticInvokeExpr(StaticInvokeExpr v)
                    {
                        ArrayList newArgList = new ArrayList();
                        for (int i = 0; i < v.getArgCount(); i++)
                            newArgList.add(newExpr(v.getArg(i)));
                        returnedExpr.setValue
                            (newStaticInvokeExpr(v.getMethodRef(),
                                                 newArgList));
                    }

                    public void caseVirtualInvokeExpr(VirtualInvokeExpr v)
                    {
                        ArrayList newArgList = new ArrayList();
                        for (int i = 0; i < v.getArgCount(); i++)
                            newArgList.add(newExpr(v.getArg(i)));
                        returnedExpr.setValue
                            (newVirtualInvokeExpr((Local)(v.getBase()),
                                                  v.getMethodRef(),
                                                  newArgList));
                    }

                    public void caseCastExpr(CastExpr v)
                    {
                        returnedExpr.setValue(newCastExpr(newExpr(v.getOp()),
                                                          v.getType()));
                    }

                    public void caseInstanceOfExpr(InstanceOfExpr v)
                    {
                        returnedExpr.setValue(newInstanceOfExpr
                                              (newExpr(v.getOp()),
                                               v.getCheckType()));
                    }

                    public void caseNewArrayExpr(NewArrayExpr v)
                    {
                        returnedExpr.setValue(newNewArrayExpr(v.getBaseType(),
                                              v.getSize()));
                    }

                    public void caseNewMultiArrayExpr(NewMultiArrayExpr v)
                     {
                        returnedExpr.setValue(newNewMultiArrayExpr
                                              (v.getBaseType(),
                                              v.getSizes()));
                    }

                    public void caseNewExpr(NewExpr v)
                    {
                        returnedExpr.setValue(newNewExpr(v.getBaseType()));
                    }

                    public void caseLengthExpr(LengthExpr v)
                      {
                        returnedExpr.setValue(newLengthExpr
                                              (newExpr(v.getOp())));
                    }

                    public void caseNegExpr(NegExpr v)
                    {
                        returnedExpr.setValue(newNegExpr(newExpr(v.getOp())));
                    }

                    public void defaultCase(Object v)
                    {
                        returnedExpr.setValue((Expr)v);
                    }                        
                });
                return returnedExpr.getValue();
            }
        else 
            {
                if (value instanceof ArrayRef)
                    return newArrayRef(((ArrayRef)value).getBase(), 
                                       newExpr(((ArrayRef)value).getIndex()));
                if (value instanceof InstanceFieldRef)
                    return newInstanceFieldRef
                        (newExpr((((InstanceFieldRef)value).getBase())),
                         ((InstanceFieldRef)value).getFieldRef());
                /* have Ref/Value, which is fine -- not Jimple-specific. */
                return value;
            }
    }

    /** Returns an empty GrimpBody associated with method m. */
    public FrimpBody newBody(SootMethod m)
    {
        return new FrimpBody(m);
    }

    /** Returns a GrimpBody constructed from b. */
    public FrimpBody newBody(Body b, String phase)
    {
        return new FrimpBody(b);
    }

    public static Value cloneIfNecessary(Value val) 
    {
        if( val instanceof Local || val instanceof Constant )
            return val;
        else
            return (Value) val.clone();
    } 
    
    /**
     * This method copies deeply a value
     * 
     * @param x
     * @return
     */
    /*public static Value cloneValueDeeply(Value x){
        Value copy = Frimp.cloneIfNecessary((Value) x);
        
        HashMap bindings = new HashMap();
        
        // Clone local units.
        Iterator it = x.getUseBoxes().iterator();
        
        while(it.hasNext()) {
            ValueBox original = (ValueBox) it.next();
            
            Value cp = (Value) Frimp.cloneIfNecessary(original.getValue());

            // Add cloned unit to our trap list.
            

            // Build old <-> new mapping.
            bindings.put(original.getValue(), cp);
        }

        // backpatching all local variables.
        it = copy.getUseBoxes().iterator();
        while(it.hasNext()) {
            ValueBox vb = (ValueBox) it.next();
            if(vb.getValue() instanceof Local)
                vb.setValue((Value) bindings.get(vb.getValue()));
        }
        
        return copy;
        
    }*/
    public static String toQ(Type x){
    	return FuriaConstructDefinitions.FURIA_ftype + "(" + ")";
    }
    
    public static String toQ(SootFieldRef x){
    	return FuriaConstructDefinitions.FURIA_ffieldRef + "(" + ")";
    }
    
    public static String toQ(SootMethodRef x){
    	
    	return FuriaConstructDefinitions.FURIA_fmethodRef + "(" +  ")";
    }
    
    public static String quote(String x){
    	String res = x;
    	res = StringTools.getEscapedStringOf(x);
    	res = res.replaceAll("\\n", "n");
    	res = res.replaceAll("\\\"", "&q" );
    	res = res.replaceAll("[\"]", "&q" );
    	res = res.replace('"' , 'q' );
    	res = res.replace('"' , 'q' );
    	
    	res = res.replace('\\' , 'b' );
    	
       	return "\"" + res  + "\"";
    }
    
    public static String toQ(Value x)throws Exception {
		String res = "(x . x)";
		// exceptions (objects from soot that we didn't inherit or re-create
		// anything we change here must be changed in FuriaSemanticExpression.getObjectFromHashCode(int)
		assert x != null;
		
		if (x instanceof Qable) {	
			
			Qable t = (Qable) x;
			res = t.toQ();
			
		} else if (x instanceof Local) {
			
			res = FuriaConstructDefinitions.FURIA_flocalRef + "(" + ")";
			
		}else if (x instanceof NullConstant) {
			
			res = FuriaConstructDefinitions.FURIA_fnull + "()";
			
		}else if (x instanceof ParameterRef) {
			
			ParameterRef p = (ParameterRef)x;			
			res = FuriaConstructDefinitions.FURIA_fparameterRef + "("  + ")";
			
		}else if (x instanceof JNewExpr) {
			JNewExpr n = (JNewExpr)x;
			
			res = FuriaConstructDefinitions.FURIA_fnew + "("  +")";
			
		}else if (x instanceof IntConstant) {
			
			res = FuriaConstructDefinitions.FURIA_fintConstant + "(" + formatNum((IntConstant)x) + ")";
			
		}else if (x instanceof FloatConstant) {
			
			res = FuriaConstructDefinitions.FURIA_ffloatConstant + "(" + formatNum((FloatConstant)x) + ")";
			
		}else if (x instanceof DoubleConstant) {
			
			res = FuriaConstructDefinitions.FURIA_fdoubleConstant +  "(" + formatNum((DoubleConstant)x) + ")";
				
		}else if (x instanceof JCaughtExceptionRef) {
			
			res = FuriaConstructDefinitions.FURIA_fcaughtException + "()";
				
		}else if (x instanceof StringConstant) {
			
			res = FuriaConstructDefinitions.FURIA_fstringConstant + "("  + ")";
			
		}else if (x instanceof StaticFieldRef) {
			
			StaticFieldRef s = (StaticFieldRef)x;
			
			res = FuriaConstructDefinitions.FURIA_fstaticFieldRef + "(" +")"; 
					
		}else if (x instanceof ThisRef) {
			
			res =  FuriaConstructDefinitions.FURIA_fthisRef + "(" + ")";
		}else if (x instanceof LongConstant){
			
			res = FuriaConstructDefinitions.FURIA_flongConstant + "(" + formatNum((LongConstant)x) + ")";
			
		}else if(x instanceof ClassConstant){
			
			res = FuriaConstructDefinitions.FURIA_fclassConstant + "(" + ")";
			
		}else {
			String msg = "This value is not Q-able: " + x
					+ " Value class name: " + x.getClass().getName();
			logger.error(msg);
			logger.error("This furia process is screwed up from now on!");
			assert false : msg;
		}

		return res;
	}
    
    public static String formatNum(LongConstant x){
    	float r = x.value;
    	return "" + r;
    }
    
    public static String formatNum(FloatConstant x){
    	float r = x.value;
    	return "" + r;
    }
    
    public static String formatNum(IntConstant x){
    	int r = x.value;
    	return "" + r;
    }
    
    public static String formatNum(DoubleConstant x){
    	double r = x.value;
    	return "" + r;
    }
    

    
    public static List convertUseBoxesListToValues(List useBoxes){
    	Iterator it = useBoxes.iterator();
    	List res = new LinkedList();
    	while(it.hasNext()){
    		Value v = ((ValueBox)it.next()).getValue();
    		res.add(v);
    	}
    	return res;
    }
    
}
