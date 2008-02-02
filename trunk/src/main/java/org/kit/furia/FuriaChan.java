package org.kit.furia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.ajmm.obsearch.asserts.OBAsserts;
import org.ajmm.obsearch.example.HelpException;
import org.ajmm.obsearch.exception.AlreadyFrozenException;
import org.ajmm.obsearch.exception.IllegalIdException;
import org.ajmm.obsearch.exception.OBException;
import org.ajmm.obsearch.exception.OutOfRangeException;
import org.ajmm.obsearch.index.IndexFactory;
import org.ajmm.obsearch.index.IndexShort;
import org.ajmm.obsearch.index.PPTreeShort;
import org.ajmm.obsearch.index.UnsafeNCorePPTreeShort;
import org.ajmm.obsearch.index.UnsafePPTreeShort;
import org.ajmm.obsearch.index.pivotselection.AcceptAll;
import org.ajmm.obsearch.index.pivotselection.KMeansPPPivotSelector;
import org.ajmm.obsearch.ob.OBShort;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.LogManager;
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
 * FuriaChan This class wraps IRIndex and OBSearch to perform matches on binary
 * programs. This class contains the ideas presented in the paper:
 * 
 * <pre>
 *  Fast Approximate Matching of Programs for Protecting
 *  Libre/Open Source Software by Using Spatial Indexes 
 *  Arnoldo Jose Muller Molina and Shinohara, Takeshi 
 *  Kyushu Institute of Technology, Japan.
 *  In: Source Code Analysis and Manipulation, 2007. SCAM 2007.
 * </pre>
 * 
 * The only difference is that instead of a spatial index, we use an asymmetric
 * P+Tree (OBSearch). This class operates on folders of folders that contain
 * fragment files, or with fragment files directly. In any case, it can load
 * applications or search fragment multi-sets The class can be invoked from the
 * command line. Each method will explains the command line parameters required
 * to invoke the method. In general, it is faster to operate on folders of
 * folders of fragment files. This is because the databases have to be loaded
 * each time the program is started. Additionally, OBsearch takes advantage of
 * frequently accessed objects that are kept in a cache. Since we are dealing
 * with trees, it makes a huge difference to use this cache. This program has
 * two modes, insert mode and search mode. - In insert mode, one or more
 * fragmented applications are added to the database. - In search mode, queries
 * of fragments searched in the database, and the corresponding binary program
 * similarity results are returned. Before search mode can be used, a "freeze"
 * operation must be performed so that OBSearch can efficiently search trees. It
 * is recommended to freeze the database after many fragmented applications have
 * been inserted. It will take some time, but it is a one time operation. Insert
 * mode and search mode work in two modes: Single application mode: One program
 * is inserted/searched. Directory of applications mode: A directory that
 * contains directories with fragmented applications is inserted/searched. The
 * second is the recommended mode. This is because in single application mode,
 * all the database has to be loaded several times.
 * @author Arnoldo Jose Muller Molina
 */

public class FuriaChan
        extends AbstractFuriaChanCommandLine {

    private static final Logger logger = Logger.getLogger("FuriaChan");

    public static void main(String[] args) throws Exception {
        int returnValue = 0;
        FuriaChanEngine engine = null;
        try {

            initLogger();
            final CommandLine cline = getCommandLine(initCommandLine(),
                    FuriaChan.class, args);

            File db = new File(cline.getOptionValue("db"));

            

            engine = new FuriaChanEngine(db);
            if(cline.hasOption("learn")){
                OBAsserts.chkFileExists(db);
                throw new Exception("Cannot freeze now at this point. The first insert will freeze so make sure it has a bunch of apps");
                //engine.freeze();
            }else{ 
                File input = new File(cline.getOptionValue("input"));
                OBAsserts.chkFileExists(db);
                OBAsserts.chkFileExists(input);
                if(cline.hasOption("load")){ // load data into the DB
                    engine.insert(input);
                    engine.freeze();
                }else if(cline.hasOption("search")){ // search for 
                    if(cline.hasOption("k")){
                        engine.setK(Byte.parseByte(cline.getOptionValue("k")));
                    }
                    if(cline.hasOption("r")){
                        engine.setR(Short.parseShort(cline.getOptionValue("r")));
                    }
                    
                    if(cline.hasOption("n")){
                        engine.setN(Short.parseShort(cline.getOptionValue("n")));
                    }
                    if(cline.hasOption("validate")){
                        engine.setValidate(true);
                    }
                    if(cline.hasOption("msetT")){
                        engine.setMSetScoreThreshold(Float.parseFloat(cline.getOptionValue("msetT")));
                    }
                    engine.search(input);
                }else{
                    throw new Exception("Operation mode is missing. Accepted values: search, load, learn");
                }
            }
        } catch (final ParseException exp) {
            logger.fatal("Argument parsing failed args: "
                    + Arrays.toString(args), exp);
            returnValue = 84;
        } catch (final HelpException exp) {
            // no problem, we just display the help and quit
            logger.debug("Should have shown the help msg");
        } catch (final Exception e) {
            logger.fatal("Exception caught", e);
            returnValue = 83;
            
        } 
        if(engine != null){
            engine.close();
        }
        
        LogManager.shutdown();
        System.exit(returnValue);
    }

    /**
     * Initializes the command line definition. Here we define all the command
     * line options to be received by the program.
     * @return The options of the program.
     */
    public static Options initCommandLine() {

        final Option in = OptionBuilder
                .withArgName("dir")
                .hasArg()
                .isRequired(false)
                .withDescription(
                        "Input directory where fragments (or directories with fragments) are located")
                .create("input");

        final Option search = new Option(
                "search",
                "Enables search mode. The n most similar programs will be returned for the given inputs");
        search.setRequired(false);

        final Option load = new Option("load",
                "Enables the loading of data in the database. The input option is required");
        search.setRequired(false);

        final Option learn = new Option(
                "learn",
                "OBSearch 'Leans' the database so that queries can be performed faster. This operation must be executed once and it must be executed before searching for license violations!");
        search.setRequired(false);
        
        final Option validate = new Option(
                "validate",
                "Used to generate statistics of the quality of the results Furia-chan gives. Assumes that input contains applications whose names (folder names) correspond to names of files in the database");
        search.setRequired(false);

        final Option db = OptionBuilder.withArgName("dir").hasArg().isRequired(
                true).withDescription("Directory in which the DB is located")
                .create("db");

        final Option r = OptionBuilder.withArgName("#").hasArg().isRequired(
                false).withDescription(
                "Range to use. Only useful in search mode").create("r");
        final Option k = OptionBuilder.withArgName("#").hasArg().isRequired(
                false).withDescription("k for the nearest neighbor search")
                .create("k");
        final Option n = OptionBuilder.withArgName("#").hasArg().isRequired(
                false).withDescription(
                "Retrieve the top n closest programs only").create("n");
        
        final Option msetT = OptionBuilder.withArgName("#").hasArg().isRequired(
                false).withDescription(
                "Multi-set threshold").create("msetT");

        Options options = new Options();
        options.addOption(in);
        options.addOption(db);
        options.addOption(n);
        options.addOption(k);
        options.addOption(r);
        options.addOption(learn);
        options.addOption(search);
        options.addOption(load);
        options.addOption(validate);
        options.addOption(msetT);
        return options;
    }

}
