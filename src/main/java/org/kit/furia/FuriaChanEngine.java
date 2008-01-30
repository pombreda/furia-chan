package org.kit.furia;

import hep.aida.bin.StaticBin1D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.ajmm.obsearch.asserts.OBAsserts;
import org.ajmm.obsearch.exception.NotFrozenException;
import org.ajmm.obsearch.exception.OBException;
import org.ajmm.obsearch.index.IndexFactory;
import org.ajmm.obsearch.index.IndexShort;
import org.ajmm.obsearch.index.PPTreeShort;
import org.ajmm.obsearch.index.UnsafePPTreeShort;
import org.ajmm.obsearch.index.pivotselection.AcceptAll;
import org.ajmm.obsearch.index.pivotselection.KMeansPPPivotSelector;
import org.apache.log4j.Logger;
import org.kit.furia.exceptions.IRException;
import org.kit.furia.fragment.OBFragment;
import org.kit.furia.index.FIRIndexShort;
import org.kit.furia.io.FuriaInputOBFragment;

import com.sleepycat.je.DatabaseException;

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
 * FuriaChanEngine holds the logic necessary to open fragmented programs insert
 * them, and search them in the database.
 * @author Arnoldo Jose Muller Molina
 */

public class FuriaChanEngine {

    private static final Logger logger = Logger.getLogger("FuriaChanEngine");

    /**
     * Minimum number of different fragments that a program must hold to be
     * retrieved successfully.
     */
    public static int MIN_DOC_SIZE = 100;

    /**
     * Folder name where OB will reside.
     */
    protected static String OBSEARCH_FOLDER = "obsearch";

    protected static String IRINDEX_FOLDER = "irindex";

    /**
     * OBsearch index of our database.
     */

    protected IndexShort < OBFragment > index;

    /**
     * Multi-set index. The information retrieval engine that calculates the
     * similarity on documents or multi-sets of OBFragments.
     */
    protected IRIndexShort < OBFragment > mIndex;

    /**
     * The number of elements to get from OBSearch.
     */
    private byte k = 1;

    /**
     * Range to use for the tree distance function.
     */
    private short r = 7;

    /**
     * Get the top n elements.
     */
    private short n = 10;

    /**
     * Validation mode assumes that the query's document names are the same
     * names as in the DB. Based on this, it creates a summary of the queries
     * whose corresponding documents were found in the DB. For this to make
     * sense, all the apps in the DB should be different. Adding different
     * versions of a program might not give the indented effect.
     */
    private boolean validationMode = false;

    public boolean isValidationMode() {
        return validationMode;
    }

    public void setValidate(boolean validationMode) {
        this.validationMode = validationMode;
    }

    /**
     * Creates a FuriaChan object. If the given directory does not exist, the
     * directory will be created and two folders (one for OBSearch and one for
     * Lucene) will be created beneath it. If the directory exists, then the
     * corresponding OBSearch index and the IRIndex index will be loaded.
     * @param directory
     *                the database directory that will be used.
     * @throws IOException
     *                 If directory does not exist and it cannot be created.
     * @throws OBException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NotFrozenException
     */
    public FuriaChanEngine(File directory) throws IOException,
            DatabaseException, NotFrozenException, IllegalAccessException,
            InstantiationException, OBException {
        File obFolder = new File(directory, OBSEARCH_FOLDER);
        File irFolder = new File(directory, IRINDEX_FOLDER);
        if (!directory.exists()) {
            directoryCreation(directory);
            directoryCreation(obFolder);
            directoryCreation(irFolder);
            index = createIndex(obFolder);
        } else { // load OBsearch and IRIndex
            OBAsserts.chkFileExists(obFolder);
            OBAsserts.chkFileExists(irFolder);
            // TODO: Fix "PPTreeShort". For this, OBSearch has to be modified. it should
            // accept a filename for the "spore" (metadata) file.
            index = (UnsafePPTreeShort < OBFragment >) IndexFactory
                    .createFromXML(readString(new File(obFolder, "PPTreeShort")));
            index.relocateInitialize(null);
        }
        mIndex = new FIRIndexShort < OBFragment >(index, irFolder);
    }
    
    public void close() throws IRException{
        mIndex.close();
    }

    /**
     * Freeze the index. After the index is frozen, matches can be performed.
     * @throws IRException
     *                 If there is an error in the freezing process.
     */
    public void freeze() throws IRException {
        mIndex.freeze();
    }

    /**
     * Insert the given directory into Furia-chan We will ignore applications
     * that have less than
     * @param dir
     *                The dir of an application or a directory of applications.
     * @throws IOException
     * @throws IRException
     */
    public void insert(File dir) throws IOException, IRException {
        FuriaInputOBFragment reader = new FuriaInputOBFragment(dir);
        Iterator < Document < OBFragment >> it = reader
                .getDocumentsFromDirectory();
        while (it.hasNext()) {
            long prevTime = System.currentTimeMillis();
            Document < OBFragment > toAdd = it.next();
            logger.debug("Loaded: " + toAdd.getName() + " size: "
                    + toAdd.size() + " msec: "
                    + (System.currentTimeMillis() - prevTime));
            if (toAdd.size() >= MIN_DOC_SIZE) {
                mIndex.insert(toAdd);
            }
        }
    }

    /**
     * Performs a search in the database and prints the result to the user.
     * @param dir
     * @return The FuriaPrecision value (queries found in the top n results /
     *         total of queries)
     * @throws IOException
     * @throws IRException
     */
    public float search(File dir) throws IOException, IRException {
        FuriaInputOBFragment reader = new FuriaInputOBFragment(dir);
        Iterator < Document < OBFragment >> it = reader
                .getDocumentsFromDirectory();
        int foundResults = 0; // only meaningful in validationMode
        int totalDocs = 0;
        StaticBin1D setScoreStats = new StaticBin1D(); //statistics on sets scores.
        StaticBin1D mSetScoreStats = new StaticBin1D(); //statistics on multi-sets scores.
        StaticBin1D nStats = new StaticBin1D(); //statistics on n
        
        logger.info("(name, luceneScore, scoreMSet, scoreSet)");
        while (it.hasNext()) {
            Document < OBFragment > toSearch = it.next();
            if (toSearch.size() >= MIN_DOC_SIZE) {
                totalDocs++;
                long prevTime = System.currentTimeMillis();
                List < ResultCandidate > result = mIndex.search(toSearch, k, r,
                        n);
                logger.info("||Match for " + toSearch.getName()
                        + " || time sec:" +  ((System.currentTimeMillis() - prevTime)/ 1000));
                Iterator < ResultCandidate > it2 = result.iterator();
                int nth = 1;
                while (it2.hasNext()) {
                    ResultCandidate resultCandidate = it2.next();
                    String docName = resultCandidate.getDocumentName();
                    // hightlight the matched result.
                    if (validationMode && docName.equals(toSearch.getName())) {
                        docName = "<<" + docName + ">>";
                        foundResults++;      
                        setScoreStats.add(resultCandidate.getNaiveScoreSet());
                        mSetScoreStats.add(resultCandidate.getNaiveScoreMSet());
                        nStats.add(nth);
                    }
                    logger.info(docName
                            + " " + resultCandidate.getScore()  
                            + " " + resultCandidate.getNaiveScoreMSet()
                            //+ " " + resultCandidate.getMSetFoundFragments()
                            //+ " " + resultCandidate.getMSetFragmentsCount()
                            + " " + resultCandidate.getNaiveScoreSet()
                            //+ " " + resultCandidate.getSetFoundFragments()
                            //+ " " + resultCandidate.getSetFragmentsCount()
                        );
                }
                nth++;
            }
        }
        float result = ((float) foundResults / (float) totalDocs);
        // validationMode's summary
        if (validationMode) {
            logger
                    .info("*** FuriaPrecision: (% of programs found in the first n documents) "
                            + result + " " + foundResults + " of " + totalDocs);
            
            logger.info("MSet. Mean: " + mSetScoreStats.mean() + " Std. Dev " + mSetScoreStats.standardDeviation());
            logger.info("Set. Mean: " + setScoreStats.mean() + " Std. Dev " + setScoreStats.standardDeviation());
            logger.info("N. Mean: " + nStats.mean() + " Std. Dev " + nStats.standardDeviation());
            // TODO: Add more statistics. Average n. Average naive score.
            // Average difference between score A and B.
        }
        return result;
    }

    /**
     * Reads a String from the given file.
     * @param file
     *                File to Read
     * @return A String representation of the file
     * @throws IOException
     *                 If there is an IO error
     */
    public static String readString(final File file) throws IOException {
        final StringBuilder res = new StringBuilder();
        final BufferedReader metadata = new BufferedReader(new FileReader(file));
        String r = metadata.readLine();
        while (r != null) {
            res.append(r);
            r = metadata.readLine();
        }
        metadata.close();
        return res.toString();
    }

    /**
     * Utility class for creating the given directory
     * @param directory
     *                creates the given directory
     * @throws IOException
     *                 If the directory cannot be created
     */
    private void directoryCreation(File directory) throws IOException {
        if (!directory.mkdirs()) {
            throw new IOException("Could not create directory"
                    + directory.toString());
        }
    }

    /**
     * A convenience method that creates an OBSearch index optimized for our
     * distance function.
     * @param folder
     * @return an optimized index
     */
    protected IndexShort < OBFragment > createIndex(File folder)
            throws IOException, DatabaseException {

        KMeansPPPivotSelector < OBFragment > ps = new KMeansPPPivotSelector < OBFragment >(
                new AcceptAll < OBFragment >());
        ps.setRetries(1);

        return new UnsafePPTreeShort < OBFragment >(folder, (short) 30,
                (byte) 12, (short) 0,
                (short) (FuriaChanConstants.MAX_NODES_PER_FRAGMENT * 2), ps);
    }


    public void setR(short r) {
        this.r = r;
    }

    public void setN(short n) {
        this.n = n;
    }

    public void setK(byte k) {
        this.k = k;
    }

}
