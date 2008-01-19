package org.kit.furia.fragment.soot.representation.internal;

import org.kit.furia.fragment.soot.representation.*;

import soot.RefType;
import soot.Type;
import soot.jimple.Constant;
import soot.jimple.ConstantSwitch;
import soot.util.Switch;

public class FBooleanConstant extends Constant implements Qable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2676288337330539924L;
	private boolean value;
	
	public FBooleanConstant(boolean s)
    {
        this.value = s;
    }

    public static FBooleanConstant v(boolean value)
    {
        return new FBooleanConstant(value);
    }

	public Type getType()
    {
        return RefType.v("java.lang.Boolean");
    }
	
	public void apply(Switch sw)
    {
        ((ConstantSwitch) sw).defaultCase(this);
    }
	
	public String toString(){
		return "" + value;
	}
	
	public String toQ() {
		String res = toString();

		return FuriaConstructDefinitions.FURIA_fbooleanConstant + "(" + res + ")";
	}

}
