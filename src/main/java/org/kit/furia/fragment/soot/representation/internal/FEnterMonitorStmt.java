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
import soot.jimple.internal.*;
import org.kit.furia.fragment.soot.representation.*;

public class FEnterMonitorStmt extends JEnterMonitorStmt implements Qable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8104427874010384519L;

	public FEnterMonitorStmt(Value op)
    {
        super(Frimp.v().newExprBox(op));
    }

    public Object clone()  // NOPMD by amuller on 11/16/06 4:10 PM
    {
        return new FEnterMonitorStmt(Frimp.cloneIfNecessary(getOp()));
    }

	public String toQ() throws Exception {
		return FuriaConstructDefinitions.FURIA_fenterMonitor  + "(" + Frimp.toQ(super.getOp()) +")";
	}
    
    
}

