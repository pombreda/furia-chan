package org.kit.furia.index;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similar.MoreLikeThis;
import org.apache.lucene.search.similar.MoreLikeThisQuery;
import org.apache.lucene.search.similar.SimilarityQueries;
import org.kit.furia.Document;
import org.kit.furia.IRIndex;
import org.kit.furia.ResultCandidate;
import org.kit.furia.Document.DocumentElement;
import org.kit.furia.exceptions.IRException;

import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseException;

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
 * FuriousIRIndex holds the basic functionality for an Information Retrieval
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
    protected enum FieldName {
        /*
         * The words of the document (data to be stored but used by Lucene to
         * actually compute the score)
         */
        WORDS,
        /* The name of the document */
        DOC_NAME,
        /* Cardinality of the multi-set of OB objects in a document */
        MULTI_SET_SIZE,

        /* Details of the doc word-freq */
        DOC_DETAILS,

        /* Cardinality of the set of OB objects in a document */
        SET_SIZE,
    }

    /**
     * This object is used to add elements to the index.
     */
    protected IndexWriter indexWriter;

    /**
     * This object is used to read different data from the index.
     */
    protected IndexReader indexReader;

    /**
     * This object is used to search the index;
     */
    protected Searcher searcher;
    
    
    /**
     * At least the given naive mset score must be obtained to consider a term in the result.
     */
    protected float mSetScoreThreshold = 0.50f;
    
    /**
     * At least the given naive set score must be obtained to consider a term in the result.
     */
    protected float setScoreThreshold = 0f;

    /**
     * The index where all the data will be stored.
     */

    /**
     * Creates a new IR index if none is available in the given path.
     * @param dbFolder
     *                The folder in which Lucene's files will be stored
     * @throws IOException
     *                 If the given directory does not exist or if some other IO
     *                 error occurs
     */
    public AbstractIRIndex(File dbFolder) throws IOException {
        indexWriter = new IndexWriter(dbFolder, new WhitespaceAnalyzer());
        indexReader = IndexReader.open(dbFolder);
        searcher = new IndexSearcher(indexReader);
    }

    public int delete(String documentName) throws IRException {
        int res = 0;
        try {
            res = indexReader.deleteDocuments(new Term(FieldName.DOC_NAME
                    .toString(), documentName));
        } catch (Exception e) {
            throw new IRException(e);
        }
        return res;
    }
    
    /**
     * Returns true if the document corresponding
     * to x's name exists in the DB. This method is intended to be
     * used in validation mode only. 
     * @param x
     * @return true if the DB does not contain a document with name x.getName()
     */
    public boolean shouldSkipDoc(Document<O> x) throws IOException{
        return this.indexReader.docFreq(new Term(FieldName.DOC_NAME.toString(), x.getName()) )< 1;
    }
    
    private class FHitCollector extends HitCollector{
        
        PriorityQueue < ResultCandidate > candidates;
        Map < Integer, Integer > normalizedQuery;
        
        public FHitCollector(Map < Integer, Integer > normalizedQuery){
            this.candidates = new PriorityQueue < ResultCandidate >();
            this.normalizedQuery = normalizedQuery;
        }
        
        public void collect(int doc, float score) {
            
            org.apache.lucene.document.Document document = null;
            try{
                document = searcher.doc(doc);
            }catch(IOException e){
                logger.fatal("This is bad", e);
                candidates =null;
                normalizedQuery = null;
                assert false;
            }
            
            String docName = document.getField(
                    FieldName.DOC_NAME.toString()).stringValue();
            TupleInput in = new TupleInput(document.getField(
                    FieldName.MULTI_SET_SIZE.toString()).binaryValue());
            // size of the doc in the DB.
            int docSizeMSet = in.readInt();

            int docSizeSet = new TupleInput(document.getField(
                    FieldName.SET_SIZE.toString()).binaryValue())
                    .readInt();

            int cx = 0;
            int intersectionQueryMSet = 0;
            int intersectionQuerySet = 0;
            TupleInput freqs = new TupleInput(document.getField(
                    FieldName.DOC_DETAILS.toString()).binaryValue());
            while (cx < docSizeSet) {
                int wordId = freqs.readInt();
                int frecuency = freqs.readInt();
                Integer frequencyQuery = normalizedQuery.get(wordId);
                if (frequencyQuery != null) {
                    intersectionQuerySet++;
                    intersectionQueryMSet += Math.min(frecuency,
                            frequencyQuery);
                }
                cx++;
            }
            ResultCandidate can = new ResultCandidate(docName, score,
                    intersectionQueryMSet, docSizeMSet,
                    intersectionQuerySet, docSizeSet);
            
            if(can.getNaiveScoreMSet() >= mSetScoreThreshold){// && can.getNaiveScoreSet() >= setScoreThreshold){
                candidates.add(can);
            }
            
        }
        
        public List<ResultCandidate> getResults(){
            List<ResultCandidate> res = new LinkedList<ResultCandidate>();
            ResultCandidate cur;
            int total = 0;
            while (((cur = candidates.poll()) != null)) {
                res.add(cur);
            }
            return res;
        }
    }
    
    /**
     * Returns the # of documents in this DB.
     * @return
     */
    public int getSize(){
        return this.indexReader.numDocs();
    }
    
    protected List < ResultCandidate > processQueryResults(
            Map < Integer, Integer > normalizedQuery, short n)
            throws IRException {
        List < ResultCandidate > res = new LinkedList < ResultCandidate >();
        try {
            // at this stage we have created a map that holds all the matched
            // elements,
            // the initial document that is in terms of the original objects
            // (obfuscated fragments in the case of furia)
            // is now transformed in terms of the database.
            // we now generate a priority queue with the terms, in order to sort
            // them and put the most
            // relevant at the beginning of the query.

            
            if (normalizedQuery.size() > 0) {
                PriorityQueue < Word > sorted = createPriorityQueue(normalizedQuery);
                Query q = createQuery(sorted);
                FHitCollector collector = new FHitCollector(normalizedQuery);
                // now we just perform the search and return the results.
                 searcher.search(q,collector);
                 return(collector.getResults());
            }
        }catch (IOException e) {
            throw new IRException(e);
        }
        return res;
    }

    /**
     * Receives a map with normalized OB_id -> freq and returns the closest
     * elements to the original query
     * @param normalizedQuery
     *                A map that contains OB_id -> freq
     * @param n
     *                Return the top n results only.
     * @return The top n candidates.
     */
    /*protected List < ResultCandidate > processQueryResults(
            Map < Integer, Integer > normalizedQuery, short n)
            throws IRException {
        try {
            // at this stage we have created a map that holds all the matched
            // elements,
            // the initial document that is in terms of the original objects
            // (obfuscated fragments in the case of furia)
            // is now transformed in terms of the database.
            // we now generate a priority queue with the terms, in order to sort
            // them and put the most
            // relevant at the beginning of the query.

            List < ResultCandidate > res = new LinkedList < ResultCandidate >();
            if (normalizedQuery.size() > 0) {
                PriorityQueue < Word > sorted = createPriorityQueue(normalizedQuery);
                Query q = createQuery(sorted);

                // now we just perform the search and return the results.
                Hits hits = searcher.search(q);
                if(logger.isDebugEnabled()){
                    logger.info("Hits size: " + hits.length());
                }

                Iterator < Hit > it = hits.iterator();
                int i = 0;
                while (it.hasNext() && i < n) {
                    Hit h = it.next();
                    String docName = h.getDocument().getField(
                            FieldName.DOC_NAME.toString()).stringValue();
                    TupleInput in = new TupleInput(h.getDocument().getField(
                            FieldName.MULTI_SET_SIZE.toString()).binaryValue());
                    // size of the doc in the DB.
                    int docSizeMSet = in.readInt();

                    int docSizeSet = new TupleInput(h.getDocument().getField(
                            FieldName.SET_SIZE.toString()).binaryValue())
                            .readInt();

                    int cx = 0;
                    int intersectionQueryMSet = 0;
                    int intersectionQuerySet = 0;
                    TupleInput freqs = new TupleInput(h.getDocument().getField(
                            FieldName.DOC_DETAILS.toString()).binaryValue());
                    while (cx < docSizeSet) {
                        int wordId = freqs.readInt();
                        int frecuency = freqs.readInt();
                        Integer frequencyQuery = normalizedQuery.get(wordId);
                        if (frequencyQuery != null) {
                            intersectionQuerySet++;
                            intersectionQueryMSet += Math.min(frecuency,
                                    frequencyQuery);
                        }
                        cx++;
                    }
                    res.add(new ResultCandidate(docName, h.getScore(),
                            intersectionQueryMSet, docSizeMSet,
                            intersectionQuerySet, docSizeSet));
                    i++;
                }
            }
            return res;
        } catch (IOException e) {
            throw new IRException(e);
        }
    }*/

    /**
     * Repeats times times the given string, separates everything with a space
     * @param x
     * @param times
     * @return
     */
    private StringBuilder repeat(int x, int times) {
        int i = 0;
        assert times > 0;
        StringBuilder res = new StringBuilder();
        while (i < times) {
            res.append(Integer.toString(x));
            res.append(" ");
            i++;
        }
        return res;
    }
    
    

    public void insert(Document < O > document) throws IRException {
        // OBSearch index that we use for "stemming".
        assert document.size() != 0;
        try {
            Index < O > index = getIndex();
            Iterator < Document < O >.DocumentElement < O > > it = document
                    .iterator();

            StringBuilder contents = new StringBuilder();
            int i = 0;
            long prevTime = System.currentTimeMillis();
            // write the multi-set representation of the document into lucene.
            TupleOutput mSetOut = new TupleOutput();

            while (it.hasNext()) {
                Document < O >.DocumentElement < O > elem = it.next();
                Result res = index.insert(elem.getObject());
                assert res.getStatus() == Result.Status.OK
                        || res.getStatus() == Result.Status.EXISTS;
                contents.append(repeat(res.getId(), elem.getCount()));
                mSetOut.writeInt(res.getId());
                mSetOut.writeInt(elem.getCount());
                i++;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Added " + i + " fragments into OB. msec: "
                        + (System.currentTimeMillis() - prevTime));
            }

            // now we just have to create the fields
            Field contentsField = new Field(FieldName.WORDS.toString(),
                    contents.toString(), Field.Store.NO, Field.Index.TOKENIZED);
            Field docName = new Field(FieldName.DOC_NAME.toString(), document
                    .getName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            TupleOutput out = new TupleOutput();
            out.writeInt(document.multiSetSize());
            Field multiSetSize = new Field(FieldName.MULTI_SET_SIZE.toString(),
                    out.getBufferBytes(), Field.Store.YES);

            Field docDetails = new Field(FieldName.DOC_DETAILS.toString(),
                    mSetOut.getBufferBytes(), Field.Store.YES);

            TupleOutput out2 = new TupleOutput();
            out2.writeInt(document.size());
            Field setSize = new Field(FieldName.SET_SIZE.toString(), out2
                    .getBufferBytes(), Field.Store.YES);

            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
            doc.add(contentsField);
            doc.add(docName);
            doc.add(multiSetSize);
            doc.add(setSize);
            doc.add(docDetails);
            indexWriter.addDocument(doc);
        } catch (Exception e) {
            throw new IRException(e);
        }
    }

    public void freeze() throws IRException {
        try {
            logger.debug("Freezing index, OB objects: "
                    + this.getIndex().databaseSize());
            getIndex().freeze();
            logger.debug("Optimizing Lucene");
            indexWriter.optimize();
        } catch (Exception e) {
            logger.fatal("Error in freeze: ", e);
            throw new IRException(e);
        }
    }

    public void close() throws IRException {
        try {
            getIndex().close();
            indexWriter.close();
            indexReader.close();
        } catch (Exception e) {
            throw new IRException(e);
        }
    }
   /* 
    private Query createQuery(PriorityQueue < Word > q) {
        StringBuilder contents  = new StringBuilder();
        Word cur;
        int total = 0;
        while (((cur = q.poll()) != null)) {
            total += cur.getDocFreq();
            contents.append(repeat(cur.getId(), cur.getDocFreq()));
            //contents.append(cur.getId());
            //contents.append(" ");
        }
        
        MoreLikeThis yay = new MoreLikeThis(indexReader);
        yay.setAnalyzer(new WhitespaceAnalyzer());
        yay.setBoost(true);
        yay.setMaxNumTokensParsed(total);
        yay.setMaxQueryTerms(0);

        yay.setFieldNames(new String[] { FieldName.WORDS.toString() });
        Query res = null;
        try{
            res = yay.like(new ByteArrayInputStream(contents.toString().getBytes()));
        }catch(IOException e){
            logger.fatal("Error while creating query", e);
        }
        return res; 
        
        //MoreLikeThisQuery res = new MoreLikeThisQuery(contents.toString(), new String[]{ FieldName.WORDS.toString() }, new WhitespaceAnalyzer() );
        
        //res.setMaxQueryTerms(100);
        //res.setPercentTermsToMatch(0.3f);
        //return res;
        
        //Query res = null;
        //try{
        //    res = SimilarityQueries.formSimilarQuery(contents.toString(), new WhitespaceAnalyzer(), FieldName.WORDS.toString(), null);
        //}catch(IOException e){
         //   logger.fatal("Problem while creating Query", e);
        //}
        //return res;
    }*/

    /**
     * Create the "more like" query from a PriorityQueue. (This code was
     * borrowed from lucene-contrib)
     */
    private Query createQuery(PriorityQueue < Word > q) {
        BooleanQuery query = new BooleanQuery(true); // aqui va el true, da
        // mejores resultados.
        // query.setUseScorer14(true); // no effect
        query.setAllowDocsOutOfOrder(true);
        //query.setMinimumNumberShouldMatch(100);
        BooleanQuery.setMaxClauseCount(q.size());
        boolean boost = true;
        Word cur;
        int qterms = 0;
        // float bestScore = 0;
        float bestScore = 0;
        while (((cur = q.poll()) != null)) {
            TermQuery tq = new TermQuery(new Term(FieldName.WORDS.toString(),
                    cur.getId().toString()));
            
              //if (boost) { if (qterms == 0) { bestScore = cur.getScore(); }
              //float myScore = cur.getScore(); tq.setBoost(myScore / bestScore); }
             //tq.setBoost( (float)cur.getDocFreq()/ (float)q.size());
            
             
            query.add(tq, BooleanClause.Occur.SHOULD);            
            qterms++;
        }
        return query;
    }

    /**
     * Create a PriorityQueue from a word->tf map. (This code was borrowed from
     * lucene-contrib)
     * @param words
     *                a map of words keyed on the word(String) with Int objects
     *                as the values.
     * @return A priority queue ordered by the most important word.
     */
    protected PriorityQueue < Word > createPriorityQueue(
            Map < Integer, Integer > words) throws IOException {
        // have collected all words in doc and their freqs
        assert words.size() > 0;
        int numDocs = this.indexReader.numDocs();
        PriorityQueue < Word > res = new PriorityQueue < Word >(words.size());
        Similarity similarity = new DefaultSimilarity();
        Iterator < Integer > it = words.keySet().iterator();
        while (it.hasNext()) { // for every word
            Integer wordId = it.next();
            Integer repetition = words.get(wordId);
            int tf = repetition; // term freq in the source doc

            int docFreq = indexReader.docFreq(new Term(FieldName.WORDS
                    .toString(), wordId.toString()));

            if (docFreq == 0) {
                continue; // index update problem?
            }

            float idf = similarity.idf(docFreq, numDocs);
            float score = tf * idf;

            // only really need 1st 3 entries, other ones are for
            // troubleshooting
            res.add(new Word(wordId, score, idf, docFreq, tf));
        }
        return res;
    }

    /**
     * Represents an OB object.
     */
    protected class Word implements Comparable < Word > {

        /**
         * Id of the word.
         */
        private Integer id;

        /**
         * Overall score.
         */
        private float score;

        /**
         * idf.
         */
        private float idf;

        /**
         * # of docs where this word appears.
         */
        private int docFreq;

        /**
         * tf.
         */
        private int tf;

        public Word(Integer id, float score, float idf, int docFreq, int tf) {
            super();
            this.id = id;
            this.score = score;
            this.idf = idf;
            this.docFreq = docFreq;
            this.tf = tf;
        }

        public Integer getId() {
            return id;
        }

        public float getScore() {
            return score;
        }

        public float getIdf() {
            return idf;
        }

        public int getDocFreq() {
            return docFreq;
        }

        public int getTf() {
            return tf;
        }

        public int compareTo(Word w) {
            int res = 0;
            if (score < w.score) {
                res = -1;
            } else if (score > w.score) {
                res = 1;
            }// else they are equal
            return res * -1;// invert the result
        }

    }

    public float getMSetScoreThreshold() {
        return mSetScoreThreshold;
    }

    public void setMSetScoreThreshold(float setScoreThreshold) {
        mSetScoreThreshold = setScoreThreshold;
    }

    public float getSetScoreThreshold() {
        return setScoreThreshold;
    }

    public void setSetScoreThreshold(float setScoreThreshold) {
        this.setScoreThreshold = setScoreThreshold;
    }

}
