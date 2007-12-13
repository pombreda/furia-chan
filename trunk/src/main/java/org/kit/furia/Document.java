package org.kit.furia;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ajmm.obsearch.OB;
import org.kit.furia.misc.IntegerHolder;

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
 * This class is a multi-set of OB objects. If we were to store natural
 * language, a document is just a multi-set of natural language words.
 * Relationships of the words within the document are not stored.
 * @param <O>
 *                The type of OB object that will be stored in this document.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */

public class Document < O extends OB > {

    /**
     * Contains each of the objects and the amount of times they appear in the
     * document.
     */
    private Map < O, DocumentElement < O > > data;

    /**
     * The identification string of this document
     */
    private String id;
    
    /**
     * The size of the multi-set of the words of this document.
     */
    private int wordCountMultiSet;


    public String getId() {
        return id;
    }

    /**
     * Creates a document with an initial estimate of 2000 elements.
     * @param id
     *                The id of the document.
     */
    public Document(String id) {
        this(id, 2000);
    }
    
    /**
     * @return The size of the set of words contained in this document.
     */
    public int size(){
        return data.size();
    }
    
    /**
     * 
     * @return The size of the multi-set of words contained in this document.
     */
    public int multiSetSize(){
        return wordCountMultiSet;
    }

    /**
     * Creates a document.
     * @param initialCapacity
     *                The number of elements that we are expecting to hold. This
     *                is for efficiency reasons, as the Document will grow
     *                automatically if the number of elements exceeds this
     *                initial estimate.
     * @param id
     *                The id of the document.
     */
    public Document(String id, int initialCapacity) {
        data = new HashMap < O, DocumentElement < O > >(initialCapacity);
        this.id = id;
        wordCountMultiSet = 0;
    }

    /**
     * Adds a word to the document.
     * @param word
     *                The word that will be added.
     */
    public void addWord(O word) {
        DocumentElement < O > r = data.get(word);
        if (r == null) {
            // this is the first time we add this word, so
            // we should initialize the counter for "word"
            r = new DocumentElement < O >(word, new IntegerHolder(0));
            data.put(word, r);
        }
        // increment the number of words in the document.
        r.inc();
        wordCountMultiSet++;
    }

    /**
     * @return An iterator with all the elements of this document.
     */
    public Iterator < DocumentElement < O >> iterator() {
        return data.values().iterator();
    }

    /**
     * This class is used by the iterator of the Document class. It holds the O
     * object and the number of times it appears in this document.
     * @param <O>
     *                The type of OB object that will be stored in this
     *                document.
     * @author Arnoldo Jose Muller Molina
     * @since 0
     */
    public class DocumentElement < T > {
        private T object;

        private IntegerHolder count;

        public DocumentElement(T object, IntegerHolder count) {
            super();
            this.object = object;
            this.count = count;
        }

        /**
         * @return The object that composes this element of the document.
         */
        public T getObject() {
            return object;
        }

        /**
         * @return The # of times this object has appeared in the document.
         */
        public int getCount() {
            return count.getValue();
        }

        /**
         * Increments the count for object.
         */
        protected void inc() {
            count.inc();
        }
    }

}
