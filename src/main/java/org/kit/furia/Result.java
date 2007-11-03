package org.kit.furia;

/*
 OBSearch: a distributed similarity search engine
 This project is to similarity search what 'bit-torrent' is to downloads.
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
 * Class: Result
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */
class Result {

    /**
     * The name of the document of the result
     */
    private String documentName;

    /**
     * The score for the given document
     */
    private float score;

    public String getDocumentName() {
        return documentName;
    }

    public float getScore() {
        return score;
    }
}
