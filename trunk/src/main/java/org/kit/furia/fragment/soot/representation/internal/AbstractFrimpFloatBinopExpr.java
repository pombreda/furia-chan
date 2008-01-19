/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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






package org.kit.furia.fragment.soot.representation.internal;

import org.kit.furia.fragment.soot.representation.Frimp;
import org.kit.furia.fragment.soot.representation.Precedence;
import org.kit.furia.fragment.soot.representation.Qable;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;

abstract public class AbstractFrimpFloatBinopExpr
    extends AbstractFloatBinopExpr implements Precedence, Qable
{
    AbstractFrimpFloatBinopExpr(Value op1, Value op2)
    {
        this(Frimp.v().newArgBox(op1),
             Frimp.v().newArgBox(op2));
    }

    protected AbstractFrimpFloatBinopExpr(ValueBox op1Box, ValueBox op2Box)
    {
        this.op1Box = op1Box;
        this.op2Box = op2Box;
    }

    abstract public int getPrecedence();

    private String toString(Value op1, Value op2, 
                            String leftOp, String rightOp, String symbol) // NOPMD by amuller on 11/16/06 4:08 PM
    {
        if (op1 instanceof Precedence && 
            ((Precedence)op1).getPrecedence() < getPrecedence()) 
            leftOp = "(" + leftOp + ")";

	if (op2 instanceof Precedence) {
	    int opPrec = ((Precedence) op2).getPrecedence(),
		myPrec = getPrecedence();
	    
	    if ((opPrec < myPrec) ||
		((opPrec == myPrec) && ((this instanceof SubExpr) || (this instanceof DivExpr))))		
		rightOp = "(" + rightOp + ")";
	}
		return leftOp + symbol + rightOp;
	//	return "(" + leftOp + ")" + symbol + "(" + rightOp + ")";			       
        
    }

    public String toString()
    {
        Value op1 = op1Box.getValue(), op2 = op2Box.getValue();
        String leftOp = op1.toString(), rightOp = op2.toString();

        return toString(op1, op2, leftOp, rightOp, getSymbol());
    }
    
    public String toQ() throws Exception {	
    	Value op1 = op1Box.getValue(), op2 = op2Box.getValue();
    	String leftOp = Frimp.toQ(op1), rightOp = Frimp.toQ(op2);
    	//toString(op1, op2, leftOp, rightOp, getQSymbol());
        return getQSymbol() + "(" + leftOp  + "," +  rightOp + ")"; 
	}
    
    public abstract String getQSymbol();
}
