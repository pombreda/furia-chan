package org.kit.furia.fragment.asm;

import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.Value;



public class FragmentInterpreter
        extends BasicInterpreter {
    
    public Value binaryOperation(AbstractInsnNode insn, Value value1, Value value2){
        FunctionValue res = new FunctionValue(insn);
        res.addParam(value1);
        res.addParam(value2);
        return res;
    }
    
    public Value  naryOperation(AbstractInsnNode insn, List values){
        FunctionValue res = new FunctionValue(insn);
        Iterator<Value> it = values.iterator();
        while(it.hasNext()){
            Value n = it.next();
            res.addParam(n);
        }
        return res;
    }
    
    public Value ternaryOperation(AbstractInsnNode insn, Value value1, Value value2, Value value3){
        FunctionValue res = new FunctionValue(insn);
        res.addParam(value1);
        res.addParam(value2);
        res.addParam(value3);
        return res;
    }
    
    public Value  unaryOperation(AbstractInsnNode insn, Value value){
        FunctionValue res = new FunctionValue(insn);
        res.addParam(value);
        return res;        
    }
    
    /**
     * A new phi function is created from the two values. If either v or w are phis, we just create one
     * phi function from all the values.
     */
    public Value merge(Value v, Value w){
        PhiFunctionValue res = null;
        if(v instanceof PhiFunctionValue && w instanceof PhiFunctionValue){
            res =  (PhiFunctionValue)v;
            res.merge((PhiFunctionValue)w);
        }else if(v instanceof PhiFunctionValue){
            res =  (PhiFunctionValue)v;
            res.addParam(w);
        }else if(w instanceof PhiFunctionValue){
            res =  (PhiFunctionValue)w;
            res.addParam(v);
        }else{
            res = new PhiFunctionValue();
            res.addParam(v);
            res.addParam(w);
        }
        return res;
    }

}
