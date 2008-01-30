package org.kit.furia;

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
 * Result holds a match result. It contains the document name, and some score calculation.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */
/**
 * @author amuller
 *
 */
public class ResultCandidate {

    /**
     * The name of the document of the result
     */
    private String documentName;

    /**
     * The score for the given document
     */
    private float score;
      
    
    /**
     * Number of fragments of the DB document (multi-set).
     */    
    private int mSetFragmentsCount;
    
    /**
     * Number of words found (multi-set).
     */
    private int mSetFoundFragments;
    
    /**
     * Number of fragments of the DB document (set).
     */    
    private int setFragmentsCount;
    
    /**
     * Number of words found (set).
     */
    private int setFoundFragments;

    public String getDocumentName() {
        return documentName;
    }

    public float getScore(){
        return score;
    }
 
    /**
     * Returns the naive similarity score.
     * @return naive score.
     */
    public float getNaiveScoreMSet(){
        return ((float)mSetFoundFragments ) / ((float) mSetFragmentsCount);
    }
    
    /**
     * Returns the naive similarity score.
     * @return naive score.
     */
    public float getNaiveScoreSet(){
        return ((float)setFoundFragments ) / ((float) setFragmentsCount);
    }

    public ResultCandidate(String documentName, float score, int mSetFoundFragments, int mSetFragmentsCount, int setFoundFragments, int setFragmentsCount) {
        super();
        this.documentName = documentName;
        this.score = score;
        this.mSetFoundFragments = mSetFoundFragments;
        this.mSetFragmentsCount = mSetFragmentsCount;    
        this.setFoundFragments = setFoundFragments;
        this.setFragmentsCount = setFragmentsCount;
    }

    public int getMSetFragmentsCount() {
        return mSetFragmentsCount;
    }

    public int getMSetFoundFragments() {
        return mSetFoundFragments;
    }

    public int getSetFoundFragments() {
        return setFoundFragments;
    }

    public int getSetFragmentsCount() {
        return setFragmentsCount;
    }


    
    
}
