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

public class FInstanceFieldRef extends AbstractInstanceFieldRef
    implements Precedence, Qable, SpecialConstructContainer
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8993710155488116600L;

	public FInstanceFieldRef(Value base, SootFieldRef fieldRef)
    {
        super(Frimp.v().newObjExprBox(base), fieldRef);
    }

    private String toString(Value op, String opString, String rightString)
    {
        String leftOp = opString;

        if (op instanceof Precedence && 
            ((Precedence)op).getPrecedence() < getPrecedence()) 
            leftOp = "(" + leftOp + ")";
        return leftOp + rightString;
    }

    public String toString()
    {
        return toString(getBase(), getBase().toString(),
                        "." + fieldRef.getSignature());
    }

    public int getPrecedence()
    {
        return 950;
    }
    
    public Object  clone()  // NOPMD by amuller on 11/16/06 4:12 PM
    {
        return new FInstanceFieldRef(Frimp.cloneIfNecessary(getBase()), 
            fieldRef);
    }

    public String toQ() throws Exception {
		String res = FuriaConstructDefinitions.FURIA_finstanceFieldRef + "(" + Frimp.toQ(getBase()) + "," + Frimp.toQ(fieldRef) + ")";
    	// just print the field ref here...
    	
		return res;
	}

	public List getContainedSpecialConstructs() {
		List res = new LinkedList();
		res.add(getBase());
		res.add(fieldRef);
		return res;
	}
    
    
    
}
