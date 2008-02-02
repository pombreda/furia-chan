package org.kit.furia.fragment.asm;

import java.util.Set;

import org.objectweb.asm.tree.analysis.Value;

public class SelfValue implements FValue {

    public int getSize() {
        return 1;
    }
    
    public void  toFragment(StringBuilder result, Set visited){
        result.append("q");
    }

}
