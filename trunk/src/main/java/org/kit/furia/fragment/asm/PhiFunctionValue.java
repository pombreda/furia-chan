package org.kit.furia.fragment.asm;

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
	*  PhiFunctionValue represents a selection function that can return
  *  any of its parameters.
	*  
  *  @author      Arnoldo Jose Muller Molina    
  */
public class PhiFunctionValue
        extends AbstractFunction {
    
    @Override
    protected String printFunctionName() {
        return "p";
    }
    
    public void merge(PhiFunctionValue other){
        if(this != other){
            for(Value v : other.params){
                if(! this.params.contains(v)){
                    this.params.add(v);
                }
            }
        }
    }
    
    /**
     * Generate the hash code for the function name.
     */
    protected int  hashCodeFunctionName(){
        return Integer.MAX_VALUE;
    }
    
    protected boolean equalFunctions(Value x){
        if(x == this){
            return true;
        }else if(x instanceof  PhiFunctionValue){
            return true;
        }else{
            return false;
        }
    }

}
