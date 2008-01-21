package org.kit.furia;

import java.io.File;
import java.io.IOException;

import org.ajmm.obsearch.index.IndexShort;
import org.ajmm.obsearch.index.PPTreeShort;
import org.ajmm.obsearch.ob.OBShort;

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
 * FuriaChan This class wraps IRIndex and OBSearch to perform matches on binary
 * programs. This class contains the ideas presented in the paper:
 * 
 *  <pre>
 *  Fast Approximate Matching of Programs for Protecting
 *  Libre/Open Source Software by Using Spatial Indexes 
 *  Arnoldo Jose Muller Molina and Shinohara, Takeshi 
 *  Kyushu Institute of Technology, Japan.
 *  In: Source Code Analysis and Manipulation, 2007. SCAM 2007.
 *  </pre>
 * The only difference is that instead of a spatial index, we use an 
 * asymmetric P+Tree.
 * 
 * This class operates on folders of folders that contain fragment files, or with
 * fragment files directly. In any case, it can load applications or search fragment multi-sets
 * The class can be invoked from the command line. Each method will explains the command line
 * parameters required to invoke the method.
 * In general, it is faster to operate on folders of folders of fragment files. This is because the databases
 * have to be loaded each time the program is started. Additionally, OBsearch takes advantage of frequently accessed
 * objects that are kept in a cache. Since we are dealing with trees, it makes a huge difference to use this cache.
 *  
 * @author Arnoldo Jose Muller Molina
 */

public class FuriaChan {
    
    /**
     * Maximum size in nodes of a fragment.  
     */
    protected static int MAX_FRAGMENT_SIZE = 1000;
    
        
    /**
     * Creates a FuriaChan object. If the given directory does not exist,
     * the directory will be created and two folders (one for OBSearch and one for Lucene)
     * will be created beneath it. If the directory exists, then the corresponding OBSearch index
     * and the IRIndex index will be loaded.
     * @param directory the database directory that will be used.
     * @throws IOException If directory does not exist and it cannot be created.
     */
    public FuriaChan(File directory) throws IOException{
        if(! directory.exists()){
            if(! directory.mkdirs()){
                throw new IOException("Could not create directory" + directory.toString());
            }
            index = 
        }else{ // load OBsearch and IRIndex
            
        }
    }
    
    /**
     * A convenience method that creates an OBSearch index optimized
     * to our distance function.
     * @param folder
     * @return
     */
    protected IndexShort<OBShort> createIndex(String folder){
        
        KMeansPPPivotSelector < OBSlice > ps = new KMeansPPPivotSelector < OBSlice >(
                new AcceptAll < OBSlice >());
        ps.setRetries(1);
        return new UnsafeNCorePPTreeShort < OBSlice >(folder,
                (short) 30, 12, (short) 0,
                (short) (MAX_FRAGMENT_SIZE * 2),ps,Runtime.getRuntime().availableProcessors() -1);
        
    }
    
}
