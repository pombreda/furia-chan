package org.kit.furia.fragment.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.Value;

/*
 Furia-chan: An Open Source software license violation detector.    
 Copyright (C) 2008 Kyushu Institute of Technology

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * FunctionValue is
 * @author Arnoldo Jose Muller Molina
 */

public class FunctionValue extends AbstractFunction {

    private AbstractInsnNode insn;
    
    private static String[] functionNameCache = new String[10000];

    public FunctionValue(AbstractInsnNode insn) {
        super();
        this.insn = insn;
    }
    
    protected boolean equalFunctions(Value x){
        if(x == this){
            return true;
        }else if(x instanceof  FunctionValue){
            FunctionValue comp = (FunctionValue)x;
            return insn.getOpcode() == comp.insn.getOpcode();
        }else{
            return false;
        }
    }
    
    /**
     * Generate the hash code for the function name.
     */
    protected int  hashCodeFunctionName(){
        return insn.getOpcode();
    }

    protected  String printFunctionName(){
        
        if(functionNameCache[insn.getOpcode()] == null){
            String fName = (new Integer(insn.getOpcode())).toString()
            .replace("1", "a")
            .replace("2", "b")
            .replace("3", "c")
            .replace("4", "d")
            .replace("5", "e")
            .replace("6", "f")
            .replace("7", "g")
            .replace("8", "h")
            .replace("9", "i")
            .replace("0", "j")
            ; 
            functionNameCache[insn.getOpcode()]  = fName;
        }
        
       
        return functionNameCache[insn.getOpcode()];
    }
}
