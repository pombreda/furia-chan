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
	*  FPhiExpr 
	*  
  *  @author      Arnoldo Jose Muller Molina    
  */

package org.kit.furia.fragment.soot.representation.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.kit.furia.fragment.soot.representation.*;
import soot.Type;
import soot.Unit;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.shimple.PhiExpr;
import soot.shimple.Shimple;
import soot.toolkits.graph.Block;
import soot.toolkits.scalar.ValueUnitPair;
import soot.util.Switch;
/**
 * @author Arnoldo Jose Muller Molina
 * <write something useful here>
 */
public class FPhiExpr implements  PhiExpr, Qable {
	
  /**
	 * 
	 */
	private static final long serialVersionUID = 872958855836670868L;

  private static final Logger logger = Logger.getLogger(FPhiExpr.class);
	
  /**
   * A list of conditions related to each of the variables...
   * 
   */
  private List args = new ArrayList();
  protected Type type = null;
    
    
  public void add(ValueBox vb){
    args.add(vb);  	
  }
    
  public FPhiExpr(List args, List preds) {
    this(args);
  }
    
  public FPhiExpr(){
    	
  }
    
    
  public FPhiExpr(List args) {
    Iterator it = args.iterator();
    type = ((Value)args.get(0)).getType(); // NOPMD by amuller on 11/16/06 4:19 PM
    while(it.hasNext()){
      ValueBox vb = new FExprBox((Value)it.next());        	
      this.args.add(vb);
    }
  }
        
  public List getArgs(){
    assert args != null;
    return args;
  }
    
        
  public void toString(UnitPrinter up)
  {
    up.literal(this.toString());
  }
  boolean usingToString =  false;
  public String toString()
  {
    StringBuffer expr = new StringBuffer("");
    if(!usingToString){
      usingToString = true;
      expr.append(Shimple.PHI + "(");
      Iterator argPairsIt = getArgs().iterator();
      int i = 0;
      while(argPairsIt.hasNext()){
	ValueBox vb = (ValueBox)argPairsIt.next();
	Value arg = vb.getValue();
	expr.append(arg.toString());
	if(argPairsIt.hasNext())
	  expr.append(", ");
            
	i++;
      }

      expr.append(")");
      usingToString = false;
    }
    return expr.toString();
  }
    
    
  public String toQ() throws Exception
  {
    StringBuffer expr = new StringBuffer("");
    if(!usingToString){
      usingToString = true;
      expr.append(FuriaConstructDefinitions.FURIA_fphi + "(");
      Iterator argPairsIt = getArgs().iterator();
      int i = 0;
      while(argPairsIt.hasNext()){
	ValueBox vb = (ValueBox)argPairsIt.next();
	Value arg = vb.getValue();
	expr.append(Frimp.toQ(arg));            
	if(argPairsIt.hasNext())
	  expr.append(", ");
            
	i++;
      }

      expr.append(")");
      usingToString = false;
    }
    return expr.toString();
  }
    
  public List getUseBoxes()
  {
    Set set = new HashSet();    	                
    Iterator argPairsIt = getArgs().iterator();
	       
    while(argPairsIt.hasNext()){
      ValueBox vb = (ValueBox) argPairsIt.next();
      set.addAll(vb.getValue().getUseBoxes());
      set.add(vb);
    }
	        
	      
    return new ArrayList(set);
  }
    
  public Object clone(){ // NOPMD by amuller on 11/16/06 4:21 PM
    	
    List newValues = new ArrayList();
    Iterator itArgs = getArgs().iterator();
    while(itArgs.hasNext()){
      ValueBox vb = (ValueBox)itArgs.next();
      Value v = vb.getValue();
    		
    		
      newValues.add(Frimp.cloneIfNecessary(v));
    }
    
    FPhiExpr res = new FPhiExpr(newValues);
    return res;
  }
  public int getArgCount(){
    return args.size();
  }
  
  public boolean equivTo(Object o)
  {
    if(o instanceof FPhiExpr){
      FPhiExpr pe = (FPhiExpr) o;

      if(getArgCount() != pe.getArgCount())
	return false;

      for(int i = 0; i < getArgCount(); i++){
	if(!getArgValueBox(i).getValue().equivTo(pe.getArgValueBox(i).getValue()))
	  return false;
      }

      return true;
    }

    return false;
  }


   public ValueBox getArgValueBox(int i){
    	return (ValueBox) this.args.get(i);
    }    

  	public boolean addArg(Value arg, Block pred) {
		this.args.add(new FExprBox(arg));
		return true;
	}
 
  public int equivHashCode()
  {
    int hashcode = 1;
        
    for(int i = 0; i < getArgCount(); i++){
      hashcode = hashcode * 17 + getArgValueBox(i).getValue().equivHashCode();
    }

    return hashcode;
  }
    
  public void apply(Switch sw)
  {   // TODO fix this! (+ . +)
    //((ShimpleExprSwitch) sw).casePhiExpr(this);
  }
    
  public Type getType()
  {
    return type;
  }
    
  
  public int getBlockId() {
    // TODO Auto-generated method stub
    return 0;
  }

  public void setBlockId(int blockId) {
    // TODO Auto-generated method stub
		
  }

  public boolean addArg(soot.Value v ,soot.Unit u){
    add(new FExprBox(v));
    return true;
  }

public ValueUnitPair getArgBox(Block arg0) {
	// TODO Auto-generated method stub
	return null;
}

public ValueUnitPair getArgBox(int arg0) {
	// TODO Auto-generated method stub
	return null;
}

public ValueUnitPair getArgBox(Unit arg0) {
	// TODO Auto-generated method stub
	return null;
}

public int getArgIndex(Block arg0) {
	// TODO Auto-generated method stub
	return 0;
}

public int getArgIndex(Unit arg0) {
	// TODO Auto-generated method stub
	return 0;
}

public Unit getPred(int arg0) {
	// TODO Auto-generated method stub
	return null;
}

public List getPreds() {
	// TODO Auto-generated method stub
	return null;
}

public Value getValue(Block arg0) {
	// TODO Auto-generated method stub
	return null;
}

public Value getValue(int arg0) {
	// TODO Auto-generated method stub
	return null;
}

public Value getValue(Unit arg0) {
	// TODO Auto-generated method stub
	return null;
}

public List getValues() {
	// TODO Auto-generated method stub
	return null;
}

public boolean removeArg(Block arg0) {
	// TODO Auto-generated method stub
	return false;
}

public boolean removeArg(int arg0) {
	// TODO Auto-generated method stub
	return false;
}

public boolean removeArg(Unit arg0) {
	// TODO Auto-generated method stub
	return false;
}

public boolean removeArg(ValueUnitPair arg0) {
	// TODO Auto-generated method stub
	return false;
}

public boolean setArg(int arg0, Value arg1, Block arg2) {
	// TODO Auto-generated method stub
	return false;
}

public boolean setArg(int arg0, Value arg1, Unit arg2) {
	// TODO Auto-generated method stub
	return false;
}

public boolean setPred(int arg0, Block arg1) {
	// TODO Auto-generated method stub
	return false;
}

public boolean setPred(int arg0, Unit arg1) {
	// TODO Auto-generated method stub
	return false;
}

public boolean setValue(Block arg0, Value arg1) {
	// TODO Auto-generated method stub
	return false;
}

public boolean setValue(int arg0, Value arg1) {
	// TODO Auto-generated method stub
	return false;
}

public boolean setValue(Unit arg0, Value arg1) {
	// TODO Auto-generated method stub
	return false;
}

public void clearUnitBoxes() {
	// TODO Auto-generated method stub
	
}

public List getUnitBoxes() {
	// TODO Auto-generated method stub
	return null;
}

  
    
}
