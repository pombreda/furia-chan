package org.kit.furia.fragment.soot.representation.internal;

import java.util.List;


import org.kit.furia.fragment.soot.representation.*;
import soot.Local;
import soot.RefType;
import soot.Type;
import soot.UnitPrinter;
import soot.jimple.internal.JimpleLocal;
import soot.util.Switch;

public class FSelfReference extends JimpleLocal implements  Qable{

	/** Constructs a JimpleLocal of the given name and type. */
    public FSelfReference()
    {
    	super("self", RefType.v());
    }
	
	public String toQ(){
		return  FuriaConstructDefinitions.FURIA_fself +"()";
	}

}
