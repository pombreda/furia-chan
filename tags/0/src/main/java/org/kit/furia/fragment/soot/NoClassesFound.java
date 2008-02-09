package org.kit.furia.fragment.soot;

import org.kit.furia.exceptions.IRException;

public class NoClassesFound
        extends IRException {
    
    public NoClassesFound(String x){
        super(x);
    }
    
    public NoClassesFound(){
        super("No classes found :(");
    }

}
