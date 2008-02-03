package org.kit.furia.fragment.asm;

import java.util.Set;

import org.kit.furia.misc.IntegerHolder;
import org.objectweb.asm.tree.analysis.Value;

public class SelfValue implements FValue {

    public int getSize() {
        return 1;
    }
    
    public void  toFragment(StringBuilder result, Set visited, IntegerHolder h, int max){
        result.append("q");
    }

}
