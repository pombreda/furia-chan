package org.kit.furia;

import org.ajmm.obsearch.ob.OBShort;
import org.kit.furia.exceptions.IRException;

import java.util.List;

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
 * An IRIndex that can store documents composed of Objects OBShort.
 * @author Arnoldo Jose Muller Molina
 * @param <
 *                O > The OB object that composes the documents to be stored.
 * @since 0
 */
public interface IRIndexShort < O extends OBShort > extends IRIndex < O > {

    /**
     * Searches the top n documents that are closest to document. For each
     * "word" (element) that composes document, at most k closest objects will
     * be returned, and all of the returned objects will be used to create the
     * query. The k
     * @param document
     *                A multi-set of objects OBShort.
     * @param k
     *                k for the nearest neighbor search
     * @param r
     *                range for the nearest neighbor search
     * @param n
     *                Maximum # of elements to return
     * @throws IRException
     *                If something goes really wrong.
     * @return The closest elements to document. The leftmost document is the
     *         closest document.
     */
    public List < ResultCandidate > search(Document < O > document, byte k, short r,
            short n) throws IRException;

}
