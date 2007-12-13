package org.kit.furia.exceptions;

/*
 Furia-chan: An Open Source software license violation detector.    
 Copyright (C) 2007 Kyushu Institute of Technology

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
 * IRException is an exception wrapper. It holds exceptions thrown by the underlying Information Retrieval
 * system implementation.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */
public class IRException
        extends Exception {
    /**
     * Internal exception
     */
    private Exception e;
    
    public IRException(Exception e){
        this.e = e;
    }
    
    public String toString(){
        if(e != null){
        return e.toString();
        }else{
            return "N/A";
        }
    }
}
