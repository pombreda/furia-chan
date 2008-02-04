package org.kit.furia;

import hep.aida.bin.StaticBin1D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
        File sporeFile = new File(obFolder, "PPTreeShort");
        if (!directory.exists()) {
            directoryCreation(directory);
            directoryCreation(obFolder);
            directoryCreation(irFolder);
            index = createIndex(obFolder);
        } else { // load OBsearch and IRIndex
            OBAsserts.chkFileExists(obFolder);
            OBAsserts.chkFileExists(irFolder);
            // TODO: Fix "PPTreeShort". For this, OBSearch has to be modified.
            // it should
            // accept a filename for the "spore" (metadata) file.
            index = (UnsafePPTreeShort < OBFragment >) IndexFactory
                    .createFromXML(readString(sporeFile));
            index.relocateInitialize(null);
        }
        mIndex = new FIRIndexShort < OBFragment >(index, irFolder);
    }

    public void close() throws IRException {
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
            if (toAdd.size() >= FuriaChanConstants.MIN_DOC_SIZE) {
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
        logger.debug("Starting search with n:" + n + " r: " + r + " k: " + k
                + " validation: " + this.validationMode + " msetThreshold "
                + mIndex.getMSetScoreThreshold() + " setThreshold "
                + mIndex.getSetScoreThreshold());
        FuriaInputOBFragment reader = new FuriaInputOBFragment(dir);
        Iterator < Document < OBFragment >> it = reader
                .getDocumentsFromDirectory();
        int foundResults = 0; // only meaningful in validationMode
        int totalDocs = 0;
        // ********************************************************
        // The following applies to queries that are found within n top docs
        StaticBin1D setScoreStats = new StaticBin1D(); // statistics on sets
                                                        // scores.
        StaticBin1D mSetScoreStats = new StaticBin1D(); // statistics on
                                                        // multi-sets scores.
        StaticBin1D nStats = new StaticBin1D(); // statistics on n
        StaticBin1D objectsPerSecond = new StaticBin1D(); // statistics on n
        int maxSizeOfAppsNotFound = 0;
        StaticBin1D maxSizeStatsOfAppsNotFound = new StaticBin1D();
        // The following are stats that apply only for real answers found after
        // n
        StaticBin1D notMatchedMSet = new StaticBin1D(); // MSet score when the
                                                        // query is not in n
        StaticBin1D notMatchedSet = new StaticBin1D(); // Set score when the
                                                        // query is not in n
        StaticBin1D notMatchedN = new StaticBin1D(); // Position when the
                                                        // result is not found
                                                        // in n
        int notMatchedFountAfter = 0; // amount of not matched found after n.
        StaticBin1D completelyUnableToFindSize = new StaticBin1D(); // the guys
                                                                    // that even
                                                                    // after
                                                                    // extending
                                                                    // n can't
                                                                    // be found
        // The following are stats that apply only to apps that are not
        // candidates but are within the top
        // n results
        StaticBin1D notMatchedMSetWithinN = new StaticBin1D(); // MSet score
                                                                // when the
                                                                // query is not
                                                                // in n
        StaticBin1D notMatchedSetWithinN = new StaticBin1D(); // Set score
                                                                // when the
                                                                // query is not
                                                                // in n
        // The following are stats that apply only to the apps that are not the
        // candidate that
        // are found after n
        StaticBin1D notMatchedMSetAfterN = new StaticBin1D(); // MSet score
                                                                // when the
                                                                // query is not
                                                                // in n
        StaticBin1D notMatchedSetAfterN = new StaticBin1D(); // Set score
                                                                // when the
                                                                // query is not
                                                                // in n
        // ********************************************************
        logger.info("# of docs" + this.mIndex.getSize());
        try{
        logger.info("# of words" + this.mIndex.getWordsSize());
        }catch(DatabaseException d){
            // :)
        }
        logger.info("(name, luceneScore, scoreMSet, scoreSet, size)");
        NumberFormat f = new DecimalFormat("0.000");
        short nToUse = n;
        int notFound = 0; // # of guys that were not even found in the list of candidates.
        if (this.validationMode) {
            mIndex.setValidationMode(true);
            nToUse = (short) (n + mIndex.getSize()); // used to get the
                                                        // unmatched guys and
                                                        // get statistics about
                                                        // them
        }
        while (it.hasNext()) {
            Document < OBFragment > toSearch = it.next();
            if (validationMode) {
                if (mIndex.shouldSkipDoc(toSearch)) {
                    logger.info("Validation mode: skipping:"
                            + toSearch.getName());
                    continue;
                }
            }
            if (toSearch.size() >= FuriaChanConstants.MIN_DOC_SIZE) {
                totalDocs++;
                long prevTime = System.currentTimeMillis();

                List < ResultCandidate > result = mIndex.search(toSearch, k, r,
                        nToUse);
                float time = (float)(System.currentTimeMillis() - prevTime) /  (float)1000;
                logger.info("|| Match for " + toSearch.getName() + " sec:"
                        + time + " MSet: " + toSearch.multiSetSize() + " Set:"
                        + toSearch.size());
                if (time > 0) {
                    objectsPerSecond.add((float) toSearch.size()
                            / time);
                }
                Iterator < ResultCandidate > it2 = result.iterator();
                int nth = 1;
                boolean found = false;
              
                logger.info("Total results:" + result.size());
                while (it2.hasNext() && nth <= this.n) {
                    ResultCandidate resultCandidate = it2.next();
                    String pre = "";
                    // hightlight the matched result.
                    if (validationMode
                            && resultCandidate.getDocumentName().equals(
                                    toSearch.getName())) {
                        foundResults++;
                        setScoreStats.add(resultCandidate.getNaiveScoreSet());
                        mSetScoreStats.add(resultCandidate.getNaiveScoreMSet());
                        nStats.add(nth);
                        found = true;
                        pre = "<<";
                    } else if (validationMode) {
                        notMatchedMSetWithinN.add(resultCandidate
                                .getNaiveScoreMSet());
                        notMatchedSetWithinN.add(resultCandidate
                                .getNaiveScoreSet());
                    }

                    logger.info(pre + resultCandidate.toString());

                    nth++;
                }

                // check if the item was found
                if (validationMode && !found) {
                    if (maxSizeOfAppsNotFound < toSearch.size()) {
                        maxSizeOfAppsNotFound = toSearch.size();
                    }
                    maxSizeStatsOfAppsNotFound.add(toSearch.size());
                    boolean found2 = false;
                    while (it2.hasNext()) {
                        ResultCandidate resultCandidate = it2.next();
                        String docName = resultCandidate.getDocumentName();
                        // hightlight the matched result.
                        if (docName.equals(toSearch.getName())) {
                            found2 = true;
                            notMatchedMSet.add(resultCandidate
                                    .getNaiveScoreMSet());
                            notMatchedSet.add(resultCandidate
                                    .getNaiveScoreSet());
                            notMatchedN.add(nth);
                            logger.info(":(:(:( Found! pos: " + nth + " "
                                    + resultCandidate.toString());
                            break;
                        } else {
                            notMatchedMSetAfterN.add(resultCandidate
                                    .getNaiveScoreMSet());
                            notMatchedSetAfterN.add(resultCandidate
                                    .getNaiveScoreSet());
                        }
                        nth++;
                    }
                    if (!found2) {
                        completelyUnableToFindSize.add(toSearch.size());
                        logger.info(":(:(:(not found :( ");
                        notFound++;
                    }
                }
            } else {
                logger.warn(toSearch.getName()
                        + " ignored because it is too small");
            }

        }
        float result = ((float) foundResults / (float) totalDocs);
        // validationMode's summary
        if (validationMode) {
            logger
                    .info("*** FuriaPrecision: (% of programs found in the first n documents) "
                            + result + " " + foundResults + " of " + totalDocs);

            printStats("MSet. Mean: ", mSetScoreStats);
            printStats("Set. Mean: ", setScoreStats);
            printStats("N. Mean: ", nStats);
            printStats("OBs per sec: ", objectsPerSecond);
            printStats("OBs not found (size). Mean: ",
                    maxSizeStatsOfAppsNotFound);

            printStats("Not matched (within N) MSet. Mean: ",
                    notMatchedMSetWithinN);
            printStats("Not matched (within N) Set. Mean: ",
                    notMatchedSetWithinN);
            printStats("Not matched (after N) MSet. Mean: ",
                    notMatchedMSetAfterN);
            printStats("Not matched (after N) Set. Mean: ", notMatchedSetAfterN);
            printStats(":(:(:(MSet. Mean: ", notMatchedMSet);
            printStats(":(:(:(Set. Mean: ", notMatchedSet);
            printStats(":(:(:(Nth. Mean: ", notMatchedN);
            printStats("Not in the results! ", completelyUnableToFindSize);
            logger.info("Not found count: " + notFound);

            // TODO: Add more statistics. Average n. Average naive score.
            // Average difference between score A and B.
        }
        return result;
    }

    private void printStats(String msg, StaticBin1D stats) {
        logger.info(msg + " " + stats.mean() + " StdDev: "
                + stats.standardDeviation() + " min: " + stats.min() + " max: "
                + stats.max());
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
        directory.mkdirs();
        OBAsserts.chkFileExists(directory);
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

    public void setN(short n) throws OBException {
        OBAsserts.chkAssert(n > 0, "n should be greater than 0");
        this.n = n;
    }

    public void setK(byte k) {
        this.k = k;
    }

    public byte getK() {
        return k;
    }

    public short getR() {
        return r;
    }

    public short getN() {
        return n;
    }

    public void setValidationMode(boolean validationMode) {
        this.validationMode = validationMode;
    }

    public float getMSetScoreThreshold() {
        return mIndex.getMSetScoreThreshold();
    }

    public float getSetScoreThreshold() {
        return mIndex.getSetScoreThreshold();
    }

    public void setMSetScoreThreshold(float setScoreThreshold) {
        mIndex.setMSetScoreThreshold(setScoreThreshold);
    }

    public void setSetScoreThreshold(float setScoreThreshold) {
        mIndex.setSetScoreThreshold(setScoreThreshold);
    }

}
