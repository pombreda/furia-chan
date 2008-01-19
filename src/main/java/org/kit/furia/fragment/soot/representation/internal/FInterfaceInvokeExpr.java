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
package org.kit.furia.fragment.soot.representation.internal;

import soot.*;
import org.kit.furia.fragment.soot.representation.*;
import soot.jimple.internal.*;
import java.util.*;

public class FInterfaceInvokeExpr extends AbstractInterfaceInvokeExpr
    implements Precedence, Qable, SpecialConstructContainer
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2114680677426630298L;

	public FInterfaceInvokeExpr(Value base, SootMethodRef methodRef, List args)
    {
        super(Frimp.v().newObjExprBox(base), methodRef,
             new ValueBox[args.size()]);

        for(int i = 0; i < args.size(); i++)
            this.argBoxes[i] = Frimp.v().newExprBox((Value) args.get(i));
    }

    public int getPrecedence() { return 950; }

    private String toString(Value op, String opString, String rightString)
    {
        String leftOp = opString;

        if (getBase() instanceof Precedence && 
            ((Precedence)getBase()).getPrecedence() < getPrecedence()) 
            leftOp = "(" + leftOp + ")";
        return leftOp + rightString;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("." + methodRef.getSignature() + "(");

        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                buffer.append(", ");

            buffer.append(argBoxes[i].getValue().toString());
        }

        buffer.append(")");

        return toString(getBase(), getBase().toString(), 
                        buffer.toString());
    }

    public void toString(UnitPrinter up)
    {
        if( PrecedenceTest.needsBrackets( baseBox, this ) ) up.literal("(");
        baseBox.toString(up);
        if( PrecedenceTest.needsBrackets( baseBox, this ) ) up.literal(")");
        up.literal(".");
        up.methodRef(methodRef);
        up.literal("(");

        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                up.literal(", ");

            argBoxes[i].toString(up);
        }

        up.literal(")");
    }

    
    public Object clone()  // NOPMD by amuller on 11/16/06 4:12 PM
    {
        List argList = new ArrayList(getArgCount());

        for(int i = 0; i < getArgCount(); i++) {
            argList.add(i, Frimp.cloneIfNecessary(getArg(i)));
        }
            
        return new  FInterfaceInvokeExpr(Frimp.cloneIfNecessary(getBase()), 
            methodRef, argList);
    }
    
    public String toQ() throws Exception {
	
    	StringBuffer buffer = new StringBuffer();

        buffer.append(FuriaConstructDefinitions.FURIA_finterfaceInvoke + "(" + Frimp.toQ(methodRef) + ", " + Frimp.toQ(getBase()));

        for(int i = 0; i < argBoxes.length; i++)
        {
            
            buffer.append(", ");

            buffer.append(Frimp.toQ(argBoxes[i].getValue()));
        }

        buffer.append(")");

        return buffer.toString();
	}
    
    public List getContainedSpecialConstructs() {
		List res = new LinkedList();
		res.add(methodRef);		
		return res;
	}

}
