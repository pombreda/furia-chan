package org.kit.furia.index;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.ajmm.obsearch.Index;
import org.ajmm.obsearch.index.IndexShort;
import org.ajmm.obsearch.ob.OBShort;
import org.ajmm.obsearch.result.OBPriorityQueueShort;
import org.ajmm.obsearch.result.OBResultShort;
import org.kit.furia.Document;
import org.kit.furia.ResultCandidate;
import org.kit.furia.Document.DocumentElement;
import org.kit.furia.exceptions.IRException;

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
 * FIRIndexShort uses IR techniques to match OB objects (OBSearch objects) when
 * the objects extend from OBShort.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */
public class FIRIndexShort < O extends OBShort >
        extends AbstractIRIndex < O > implements
        org.kit.furia.IRIndexShort < O > {

    /**
     * 
     */
    private IndexShort < O > index;

    /**
     * Creates a new IR Index that works on shorts
     * @param dbFolder
     *                The folder in which Lucene's files will be stored
     * @throws IOException
     *                 If the given directory does not exist or if some other IO
     *                 error occurs
     */
    public FIRIndexShort(IndexShort < O > index, File dbFolder)
            throws IOException {
        super(dbFolder);
        this.index = index;
    }

    public final List < ResultCandidate > search(Document < O > document, byte k,
            short r, short n) throws IRException{
        Iterator < Document < O >.DocumentElement < O >> it = document
                .iterator();
        // we transform now the given document, to a document that is in terms
        // of the
        // fragments available in the database.
        // we store term id -> term freq. This will be used to create the query.
        Map<Integer, Integer> documentInTermsOfTheDatabase = new HashMap<Integer, Integer>(document.size() * k);
        int intersectionSize = 0;
        while (it.hasNext()) {
            Document < O >.DocumentElement < O > elem = it.next();
            O toMatch = elem.getObject();
            OBPriorityQueueShort < O > result = new OBPriorityQueueShort < O >(
                    k);
            try{
                // match the object in the database.
                index.searchOB(toMatch, r, result);
                intersectionSize += result.getSize() * elem.getCount();
                // for all the returned elements, we add their ids and the initial
                // count that came from "document".
                Iterator<OBResultShort<O>> itO = result.iterator();
                while(itO.hasNext()){
                    OBResultShort<O> match = itO.next();
                    Integer exists = documentInTermsOfTheDatabase.get(match.getId());
                    if(exists == null){
                        documentInTermsOfTheDatabase.put(match.getId(), elem.getCount());
                    }else{
                        documentInTermsOfTheDatabase.put(match.getId(), elem.getCount() + exists);
                    }
                }
                
                return processQueryResults(documentInTermsOfTheDatabase,n, intersectionSize, document.multiSetSize());
                
            }catch(Exception e){
                throw new IRException(e);
            }
            
        }
        
        return null;
    }
    
    
    public Index < O > getIndex() {
        return index;
    }

}
