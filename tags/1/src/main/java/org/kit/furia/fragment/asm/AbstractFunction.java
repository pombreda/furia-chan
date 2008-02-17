package org.kit.furia.fragment.asm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.kit.furia.fragment.soot.HugeFragmentException;
import org.kit.furia.misc.IntegerHolder;
import org.objectweb.asm.tree.analysis.BasicValue;
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
 * AbstractFunction holds common logic for representing functions.
 * @author Arnoldo Jose Muller Molina
 */

public abstract class AbstractFunction implements FValue{
    protected List < Value > params;
    protected Set < Value > paramSet;
    private int size;
    protected AbstractFunction(){
        super();
        params = new LinkedList<Value>();
        paramSet = new HashSet<Value>();
        size = 0;
    }
  
    /**
     * Adds a new parameter to this function.
     * @param param the new parameter.
     */
    public void addParam(Value param){
        if(!paramSet.contains(param) && this != param){
            params.add(param);
            paramSet.add(param);
            size+= param.getSize();
        }
    }
    
    public int getSize(){
        return size;
    }
    
    /**
     * Returns the string representation of the function name.
     * @return string representation of the function name.
     */
    protected abstract String printFunctionName();
    
    /**
     * Returns true if the function identifiers are equal.
     * @param x
     * @return
     */
    protected abstract boolean equalFunctions(Value x);
    
    /*public boolean equals(Object other){
        if(other == this){
            return true;
        }else if(other instanceof AbstractFunction){
            AbstractFunction other2 = (AbstractFunction)other;
            if(equalFunctions(other2)){
                // check that each element is equal.
                return this.params.equals(other2);
            }else{
                return false;
            }
        }else{
            return false;
        }
    }*/
    
    /**
     * Generate the hash code for the function name.
     */
    protected abstract int  hashCodeFunctionName();
    
    /*public int hashCode(){
        return params.hashCode() + hashCodeFunctionName();
    }*/
    
    /**
     * Prints the fragment into the given result.
     */
    public void  toFragment(StringBuilder result, Set visited, IntegerHolder count, int max) throws HugeFragmentException{
        if(count.getValue()> max){
            throw new HugeFragmentException();
        }
            
        // check for repeated elements.
        if(visited.contains(this)){
            result.append("s()");//self
            count.inc();
            return;
        }else{
            visited.add(this);
        }
        result.append(printFunctionName());
        count.inc();
        result.append("(");
        Iterator<Value> it = params.iterator();
        String comma = "";
        while(it.hasNext()){
            Value n = it.next();
            result.append(comma);
            if(n instanceof BasicValue){
                BasicValue n2 = (BasicValue)n;
                String j = n2.toString();
                if(j.equals(".")){
                    j = "z";
                }
                result.append(j);
                count.inc();
            }else if (n instanceof FValue){
                FValue n2 = (FValue)n;
                n2.toFragment(result, visited, count, max);
            }else{
                throw new IllegalArgumentException("Not supported value");
            }
            comma = ",";
        }
      
        result.append(")");
    }
}
