package org.kit.furia.index;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.ajmm.obsearch.Index;
import org.ajmm.obsearch.OB;
import org.ajmm.obsearch.Result;
import org.ajmm.obsearch.exception.AlreadyFrozenException;
import org.ajmm.obsearch.exception.IllegalIdException;
import org.ajmm.obsearch.exception.OBException;
import org.ajmm.obsearch.exception.OutOfRangeException;
import org.ajmm.obsearch.exception.UndefinedPivotsException;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.kit.furia.Document;
import org.kit.furia.IRIndex;
import org.kit.furia.Document.DocumentElement;

import com.sleepycat.bind.tuple.TupleOutput;
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
 * we utilize an information retrieval system (Apache's Lucene) to perform the
 * matching.
 * @author Arnoldo Jose Muller Molina
 * @param <O>
 *                The basic unit in which all the information is divided. In the
 *                case of natural language documents, this would be a word.
 * @since 0
 */

public abstract class AbstractIRIndex < O extends OB > implements IRIndex < O > {

    /**
     * Logger.
     */
    private static final Logger logger = Logger
            .getLogger(AbstractIRIndex.class);

    /**
     * Lucene has the concepts of fields of a document. They are analogous to
     * columns of a DB table (SQL). This enumeration lists the name of the
     * fields our IRIndex will use.
     */
    private enum FieldName {
        /*
         * The words of the document (data to be stored but used by Lucene to
         * actually compute the score)
         */
        WORDS,
        /* The name of the document */
        DOC_NAME,
        /*
         * Stored as an array of ints, used by us to compute a similarity score
         * after Lucene has done its job
         */
        WORDS_BYTE

    }

    /**
     * This object is used to add elements to the index.
     */
    private IndexWriter indexWriter;

    /**
     * Creates a new Lucene index if none is available in the given path.
     * @param dbFolder
     *                The folder in which Lucene's files will be stored
     * @throws IOException
     *                 If the given directory does not exist or if some other IO
     *                 error occurs
     */
    public AbstractIRIndex(File dbFolder) throws IOException {
        indexWriter = new IndexWriter(dbFolder, new WhitespaceAnalyzer());

    }

    public void delete(String documentName) {
        // TODO Auto-generated method stub
        assert false;
    }

    public void insert(Document < O > document) throws DatabaseException,
            OBException, IllegalAccessException, InstantiationException,
            CorruptIndexException, IOException {
        // OBSearch index that we use for "stemming".
        Index < O > index = getIndex();
        Iterator < Document < O >.DocumentElement < O > > it = document
                .iterator();
        int[] words = new int[document.size()];
        int i = 0;
        // The first item indicates the number of words in the
        // array
        TupleOutput outputWords = new TupleOutput();
        outputWords.writeInt(document.size());
        while (it.hasNext()) {
            Document < O >.DocumentElement < O > elem = it.next();
            Result res = index.insert(elem.getObject());
            words[i] = res.getId();
            outputWords.writeInt(res.getId());
            i++;
            assert res.getStatus() == Result.Status.OK
                    || res.getStatus() == Result.Status.EXISTS;
        }
        // now we just have to add the obtained words into Lucene
        // this is a bit ugly, lucene at the end only knows about strings,
        // so what we give lucene is a list of numbers (their string
        // representation)
        // separated by spaces. It would be nice to store directly the integers.
        i = 0;
        StringBuilder contents = new StringBuilder();
        for (int k : words) {
            contents.append(k);
            contents.append(" ");
        }
        // now we just have to create the fields
        Field contentsField = new Field(FieldName.WORDS.toString(), contents
                .toString(), Field.Store.NO, Field.Index.TOKENIZED);
        Field docName = new Field(FieldName.DOC_NAME.toString(), document
                .getId(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        Field contentsBytes = new Field(FieldName.WORDS_BYTE.toString(),
                outputWords.getBufferBytes(), Field.Store.YES);

        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
        doc.add(contentsField);
        doc.add(docName);
        doc.add(contentsBytes);
        indexWriter.addDocument(doc);
    }

    public void freeze() throws IOException, AlreadyFrozenException,
            IllegalIdException, IllegalAccessException, InstantiationException,
            DatabaseException, OutOfRangeException, OBException,
            UndefinedPivotsException {
        logger.debug("Freezing index");
        getIndex().freeze();
        logger.debug("Optimizing Lucene");
        indexWriter.optimize();
    }
    
    public void close() throws DatabaseException, IOException{
        getIndex().close();
        indexWriter.close();
    }

}
