package org.kit.furia;

import org.ajmm.obsearch.OB;

import java.io.IOException;
import java.util.List;
import org.ajmm.obsearch.Index;
import org.ajmm.obsearch.exception.AlreadyFrozenException;
import org.ajmm.obsearch.exception.IllegalIdException;
import org.ajmm.obsearch.exception.OBException;
import org.ajmm.obsearch.exception.OutOfRangeException;
import org.ajmm.obsearch.exception.UndefinedPivotsException;
import org.apache.lucene.index.CorruptIndexException;

import com.sleepycat.je.DatabaseException;

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
 * AbstractIRIndex holds the basic functionality for an Information Retrieval
 * system that works on OB objects (please see obsearch.berlios.de). By using a
 * distance function d, we transform the queries in terms of the closest
 * elements that are in the database, and once this transformation is performed,
 * we utilize an information retrieval system to perform the matching. Because
 * our documents are multi-sets, the distribution of OB objects inside a
 * document is taken into account.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */
public interface IRIndex < O extends OB > {

    /**
     * Inserts a new document into the database.
     * @param document
     *                The document to be inserted.
     * @throws DatabaseException
     *                 If something goes wrong with the DB
     * @throws OBException
     *                 User generated exception
     * @throws IllegalAccessException
     *                 If there is a problem when instantiating objects O
     * @throws InstantiationException
     *                 If there is a problem when instantiating objects O
     * @throws CorruptIndexException
     *                 If the IR index is corrupted.
     * @throws IOException
     *                 If there is a problem while writing into the IRIndex.
     */
    void insert(Document < O > document) throws DatabaseException, OBException,
            IllegalAccessException, InstantiationException,
            CorruptIndexException, IOException;

    /**
     * Deletes the given string document from the database.
     * @param documentName
     *                The document id that will be deleted.
     */
    void delete(String documentName);

    /**
     * Returns the underlying OBSearch index.
     * @return the underlying OBSearch index.
     */
    Index < O > getIndex();

    /**
     * Freezes the index. From this point data can be inserted, searched and
     * deleted. The index might deteriorate at some point so every once in a
     * while it is a good idea to rebuild the index. Additionally, it will
     * Optimize the IR index.
     * @throws IOException
     *                 if the index serialization process fails
     * @throws AlreadyFrozenException
     *                 If the index was already frozen and the user attempted to
     *                 freeze it again
     * @throws DatabaseException
     *                 If something goes wrong with the DB
     * @throws OBException
     *                 User generated exception
     * @throws IllegalAccessException
     *                 If there is a problem when instantiating objects O
     * @throws InstantiationException
     *                 If there is a problem when instantiating objects O
     * @throws UndefinedPivotsException
     *                 If the pivots of the index have not been selected before
     *                 calling this method.
     * @throws OutOfRangeException
     *                 If the distance of any object to any other object exceeds
     *                 the range defined by the user.
     * @throws IllegalIdException
     *                 This exception is left as a Debug flag. If you receive
     *                 this exception please report the problem to:
     *                 http://code.google.com/p/obsearch/issues/list
     */
    void freeze() throws IOException, AlreadyFrozenException,
            IllegalIdException, IllegalAccessException, InstantiationException,
            DatabaseException, OutOfRangeException, OBException,
            UndefinedPivotsException;

    /**
     * Closes the databases. You *should* close the databases
     * after using an IRIndex.
     * @throws DatabaseException
     *                 If something goes wrong with the OBSearch
     * @throws IOException
     *                 If something goes wrong with the IR engine.
     */
    void close() throws DatabaseException, IOException;
}
