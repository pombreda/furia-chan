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

/**
 * @author Created by Arnoldo Jose Muller Molina (ID: Costa Rica:110820160)
 * arnoldoMuller@gmail.com
 */
/**
 * 
 * Since Soot seems to like BodyTransformers and I have found particularly 
 * hard to get a Body out of a method in the library, so this "Transformation"
 * steals a body out of the transformation pipe 
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
