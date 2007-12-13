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
     * Returns the following value:
     * ( |matched objects| * 2) / (|DOC| + |Query|)
     * where: "matched objects" is the size of the objects found by OB.
     * DOC is the multi-set of OB objects of the found document name.
     * Query is the multi-set of OB objects of the original query.
     * @return naive score.
     */
    private float naiveScore;

    public String getDocumentName() {
        return documentName;
    }

    /**
     * Returns the score calculated by the IR engine
     * @return score calculated by the IR engine
     */
    public float getScore() {
        return score;
    }
    /**
     * Returns the following value:
     * ( |matched objects| * 2) / (|DOC| + |Query|)
     * where: "matched objects" is the size of the objects found by OB.
     * DOC is the multi-set of OB objects of the found document name.
     * Query is the multi-set of OB objects of the original query.
     * @return naive score.
     */
    public float getNaiveScore(){
        return naiveScore;
    }

    public ResultCandidate(String documentName, float score, float naiveScore) {
        super();
        this.documentName = documentName;
        this.score = score;
        this.naiveScore = naiveScore;
    }
}
