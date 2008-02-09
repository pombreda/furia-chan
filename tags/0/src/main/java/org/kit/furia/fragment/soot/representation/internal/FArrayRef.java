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
import org.kit.furia.fragment.soot.representation.PrecedenceTest;
import org.kit.furia.fragment.soot.representation.Qable;

import soot.*;
import soot.jimple.internal.*;

public class FArrayRef extends JArrayRef implements Precedence, Qable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = -7207388997738085867L;

public FArrayRef(Value base, Value index)
    {
      super(Frimp.v().newObjExprBox(base),
            Frimp.v().newExprBox(index));
    }
  
  public int getPrecedence() { return 950; }

  private String toString(Value op1, 
                          String leftOp, String rightOp) // NOPMD by amuller on 11/16/06 4:08 PM
    {
      if (op1 instanceof Precedence && 
          ((Precedence)op1).getPrecedence() < getPrecedence()) 
        leftOp = "(" + leftOp + ")";
      
      return leftOp + "[" + rightOp + "]";
    }

    public void toString( UnitPrinter up ) {
        if( PrecedenceTest.needsBrackets( baseBox, this ) ) up.literal("(");
        baseBox.toString(up);
        if( PrecedenceTest.needsBrackets( baseBox, this ) ) up.literal(")");
        up.literal("[");
        indexBox.toString(up);
        up.literal("]");
    }

  public String toString()
    {
      Value op1 = getBase(), op2 = getIndex();
      String leftOp = op1.toString(), rightOp = op2.toString();
      
      return toString(op1, leftOp, rightOp);
    }

    public Object clone()  // NOPMD by amuller on 11/16/06 4:08 PM
    {
        return new FArrayRef(Frimp.cloneIfNecessary(getBase()), Frimp.cloneIfNecessary(getIndex()));
    }

	public String toQ() throws Exception {

		
		Value op1 = getBase(), op2 = getIndex();
	    String leftOp = Frimp.toQ(op1), rightOp = Frimp.toQ(op2);
		
	    //if (op1 instanceof Precedence && 
	    //        ((Precedence)op1).getPrecedence() < getPrecedence()) 
	     //     leftOp = "(" + leftOp + ")";
	        
	        return FuriaConstructDefinitions.FURIA_farrayRef +"("+ leftOp + "," +  rightOp + ")";	    		
	}
    
    

  }













