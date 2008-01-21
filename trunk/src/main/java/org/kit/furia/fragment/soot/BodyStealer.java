/*
 * Created on Aug 12, 2005 11:33:18 PM
 *
 * by Arnoldo Jose Muller Molina: arnoldoMuller@gmail.com
 */
package org.kit.furia.fragment.soot;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import soot.*;
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
	*  BodyStealer 
	*  
  *  @author      Arnoldo Jose Muller Molina    
	* This class steals bodies from the transformation pipe, and recovers them
  * so that we can generate fragments later.
  */
public class BodyStealer extends BodyTransformer {
    
    private String signature;
    private boolean found;
    
    private List<Body> bodies;
    private boolean getAll = false;
    
    public BodyStealer(){
        this("");
        getAll = true;
        
    }
    
    public void reset(){
    	found = false;
    	signature = "";
    
        getAll = true;
        bodies = new LinkedList<Body>();
    }
    
    
    
    public BodyStealer(String methodSignature){
        signature = methodSignature;
        
        found = false;
        bodies = new LinkedList<Body>();
    }
    
    public BodyStealer(String className, String methodSignature){
        this("<" + className + ": " + methodSignature + ">");     
    }
    
    protected void addBody(Body b){
        
        bodies.add(b);
        
    }
    
    /* (non-Javadoc)
     * @see soot.BodyTransformer#internalTransform(soot.Body, java.lang.String, java.util.Map)
     */
    protected void internalTransform(Body b, String phaseName, Map options) {
       
        //G.v().out.println("Stealer In with: " + b.getMethod().getDeclaringClass().getName() + " " + b.getMethod().getSignature() );
        //signature looks like: <furia.testsamples.T: int doubleLoop(int)>
       if(getAll ||b.getMethod().getSignature().equals(signature) ){ 
          
           addBody(b);
           found = true;
           //G.v().out.println("Stealer: found your jewels");
       }
    }

    
    /**
     * Tells the stealer to steal all the bodies
     *
     */
    public void gettAll(boolean getAll){
        this.getAll = getAll;
    }

    /**
     * @return Returns the signature.
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @return Returns true if the body was found
     */
    public boolean isFound() {
        return found;
    }

    
    public Iterator<Body> getIterator(){
        return bodies.iterator();
    }

}
