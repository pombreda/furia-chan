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

import soot.*;
import org.kit.furia.fragment.soot.representation.*;
import soot.jimple.internal.*;
import java.util.*;

public class FNewMultiArrayExpr extends AbstractNewMultiArrayExpr implements Qable, SpecialConstructContainer
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7325755315456136316L;

	public FNewMultiArrayExpr(ArrayType type, List sizes)
    {
        super(type, new ValueBox[sizes.size()]);

        for(int i = 0; i < sizes.size(); i++)
            sizeBoxes[i] = Frimp.v().newExprBox((Value) sizes.get(i));
    }
    
    public Object clone()  // NOPMD by amuller on 11/16/06 4:13 PM
    {
        List clonedSizes =  new ArrayList(getSizeCount());

        for(int i = 0; i <  getSizeCount(); i++) {
            clonedSizes.add(i,  Frimp.cloneIfNecessary(getSize(i)));
        }
                                                         
        
        return new FNewMultiArrayExpr(getBaseType(), clonedSizes);
    }
    
    public String toQ() throws Exception
    {
        StringBuffer buffer = new StringBuffer();

        Type t = super.getBaseType();
        
        buffer.append(FuriaConstructDefinitions.FURIA_fnewMultiArray+ "(" + Frimp.toQ(t));
        String comma = ",";
        for(int i = 0; i < sizeBoxes.length; i++){
            buffer.append(comma + Frimp.toQ(sizeBoxes[i].getValue()));
            
        }

        //for(int i = 0; i < baseType.numDimensions - sizeBoxes.length; i++)
        //    buffer.append("[]");
        
        buffer.append(")");
        return buffer.toString();
    } 
    
    public List getContainedSpecialConstructs() {
		List res = new LinkedList();
		res.add(getBaseType());		
		return res;
	}

}
