package org.kit.furia.fragment.soot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

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
 * FragmentBuilderClientAux fragments a directory of class files. It is designed
 * to be called from the command line. This program is not intended to be called
 * by humans, only intended to be called by other programs. Receives the
 * following parameters: [1] The directory that is to be processed. [2] The
 * maximum number of expansions to allow. (To prevent huge Fragments) [3] The
 * output directory in which the fragments will be stored in a "fragments" file.
 * If the filename {@value org.kit.furia.io.AbstractFuriaInput.fragmentFileName}
 * is found in the output directory, this directory is silently skipped.[4] The
 * log configuration file that will be used.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */

public class FragmentBuilderClientAux {

    private static Logger logger = Logger.getLogger("FragmentBuilderClientAux");

    public static void main(String[] args) throws Exception{
       
        //try {
            File dir = new File(args[0]);
            int maximumNumberOfExpansions = Integer.parseInt(args[1]);
            File output = new File(args[2]);
            if (!dir.exists()) {
                String msg = "Input directory does not exist: " + dir;
                System.err.println(msg);
                throw new IOException(msg);
            }
            output.mkdirs();

            if (!output.exists()) {
                String msg = "Output directory does not exist: "
                    + output;
                System.err.println(msg);
                throw new IOException(msg);
            }
            
            Logger root = Logger.getRootLogger();
            root.addAppender(new FileAppender(
                   new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), output.toString() + File.separator +"output.txt"));
            root.setLevel(Level.DEBUG);
            
            File fragmentsFile = new File(output,
                    org.kit.furia.io.AbstractFuriaInput.fragmentFileName);
            if (fragmentsFile.exists()) {
                // nothing to do here. the file was already processed.
                System.exit(0);
            }

             FragmentExtractor
                    .extractMethodsFromDirectory(dir.toString(),
                            maximumNumberOfExpansions, 1, output.toString(), fragmentsFile.toString());
            /*Iterator < FragmentBuilder > it = result.iterator();
            FileWriter outputFile = new FileWriter(fragmentsFile);
            while (it.hasNext()) {
                FragmentBuilder fb = it.next();
                StringBuilder tmp = new StringBuilder();
                fb.generateString(tmp);
                outputFile.write(tmp.toString());
            }
            outputFile.close();*/
            logger.info("Completed Fragmentation for " + args[0]);
        /*} catch (Exception e) {
            e.printStackTrace();
            logger.fatal("Aborting", e);
            // logger.fatal("Received Env:\n" + System.getenv().toString());
            System.exit(48);
        }
       
        System.exit(0);
        */
    }
}
