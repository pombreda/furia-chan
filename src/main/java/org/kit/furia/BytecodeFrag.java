package org.kit.furia;

import java.io.File;
import java.util.Arrays;

import org.ajmm.obsearch.asserts.OBAsserts;
import org.ajmm.obsearch.example.HelpException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kit.furia.fragment.soot.FragmentBuilderClient;

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
 * BytecodeFrag is a command line utility that fragments Java byte-code.
 * The program will create n threads according to the number of processors you have
 * and will match everything in parallel.
 * If no -timeout flag is given, a default of 30 minutes timeout per application is set.
 * <div>
 * Regarding the output directory:
 * 1) If it does not exists it will be created.
 * 2) If it exists, it will be unmodified.
 * 
 * If the output directory exists:
 * if given "dm" (directory of directories mode):
 * 1) All the directories in the output folder that contain a "fragments" file (that is, if the fragments have been modified) will be ignored.
 * if not given "dm":
 * 1) The output directory will be ignored if it contains a "fragments" file.
 * </div>
 * @author Arnoldo Jose Muller Molina
 */

public class BytecodeFrag  extends AbstractFuriaChanCommandLine{
    
    private static final Logger logger = Logger.getLogger("BytecodeFrag");
    
    public static void main(String args[]){
        int returnValue = 0;        
        try {

            initLogger();
            
            final CommandLine cline = getCommandLine(initCommandLine(),
                    BytecodeFrag.class, args);
            
            boolean directoryOfDirectoriesMode = false;
            if(cline.hasOption("dm")){
                logger.info("Directory of directories mode");
                directoryOfDirectoriesMode = true;                
            }
            
            long timeout = 30 *  60  * 1000; // default value.
            if(cline.hasOption("timeout")){
                timeout = Integer.parseInt( cline.getOptionValue("timeout"));
            }
            File input = new File(cline.getOptionValue("input"));
            OBAsserts.chkFileExists(input);
            File output = new File(cline.getOptionValue("output"));
       
            FragmentBuilderClient c = new FragmentBuilderClient(directoryOfDirectoriesMode,
                    input, Runtime.getRuntime().availableProcessors(), output, false, timeout);
            
        }catch (final ParseException exp) {
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

        LogManager.shutdown();
        System.exit(returnValue);
        
    }
                
    /**
     * Initializes the command line definition. Here we define all the command
     * line options to be received by the program.
     * @return The options of the program.
     */
    public static Options initCommandLine() {
        
        final Option dm= new Option("dm", "Directory mode: If given this option, then the program will process a directory of directories of class files");
        dm.setRequired(false);

        final Option in = OptionBuilder.withArgName("dir").hasArg().isRequired(
                true).withDescription("Input directory (directory of directories if dm mode is given)").create("input");
        
        final Option output = OptionBuilder.withArgName("dir").hasArg().isRequired(
                true).withDescription("Output directory where the fragments will be stored.").create("output");
        
        final Option timeout = OptionBuilder.withArgName("seconds").hasArg().isRequired(
                false).withDescription("Timeout in seconds to give to the processing of each application").create("timeout");
        
        Options options = new Options();
        options.addOption(in);
        options.addOption(dm);
        options.addOption(output);
        options.addOption(timeout);
        return options;
    }

    
}
