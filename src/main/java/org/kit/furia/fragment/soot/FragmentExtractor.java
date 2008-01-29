package org.kit.furia.fragment.soot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;
import org.kit.furia.fragment.MTDFragmentAST;
import org.kit.furia.fragment.OBFragment;
import org.kit.furia.fragment.soot.representation.FrimpBody;
import org.kit.furia.misc.IntegerHolder;

import soot.Body;
import soot.G;
import soot.PackManager;
import soot.Transform;
import soot.grimp.Grimp;
import soot.grimp.GrimpBody;
import soot.toolkits.graph.BlockGraph;
import soot.util.cfgcmd.CFGGraphType;

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
 * FragmentExtractor takes a directory and loads SootFragmentBuilder objects
 * for each method found. From the SootFragmentBuilder object, fragments can
 * be extracted.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */

public class FragmentExtractor {
    private static final Logger logger = Logger
            .getLogger(FragmentExtractor.class);

    // this is the default graph type used in furia.
    // TODO: We could try using some other (simpler) cfg without exceptions :)
    public static final CFGGraphType defaultGraphType = CFGGraphType.EXCEPTIONAL_BLOCK_GRAPH;
    //public static final CFGGraphType defaultGraphType = CFGGraphType.BRIEF_BLOCK_GRAPH;
    private FragmentExtractor() {

    }

    public static void extractMethodsFromDirectory(
            final String directory, final int max_structures_allowed,
            final int min_structures_allowed, final String outputPath, final String outputFile)
            throws NoClassesFoundByStealer, FileNotFoundException, Exception {
        extractMethodsFromDirectory(directory, defaultGraphType,
                max_structures_allowed, min_structures_allowed, outputPath, outputFile);
    }

    /**
     * Extracts fragments from the given directory.
     * Furia-chan's fragment file format is:
     * <repetitions count>\t<fragment>
     * @param directory Directory from where we will extract fragments
     * @param graphtype The graph representation used to interpret the methods
     * @param maxStructuresAllowed maximum nodes per tree
     * @param min_structures_allowed minimum nodes per tree
     * @param outputPath Output path where logs will be written
     * @param outputFile The output fragment file that will be used
     * @throws NoClassesFoundByStealer
     * @throws FileNotFoundException
     * @throws Exception
     */
    public static void extractMethodsFromDirectory(
            final String directory, final CFGGraphType graphtype,
            final int maxStructuresAllowed, final int minStructuresAllowed,
            final String outputPath, String outputFile) throws NoClassesFoundByStealer,
            FileNotFoundException, Exception {
        final BodyStealer stealer = stealBodiesFromDir(directory, outputPath);

        if (!stealer.isFound()) {
            throw new NoClassesFoundByStealer("In directory:" + directory);
        }
        final Iterator < Body > it = stealer.getIterator();
        FileWriter output = new FileWriter(outputFile);
        // repetition counter
        HashMap<String, IntegerHolder> repetitionCounts = new HashMap<String, IntegerHolder>();
        while (it.hasNext()) {
            final Body tempBody = it.next();
            //logger.debug("Fragmenting method: " + tempBody.getMethod().toString());
            FrimpBody fb = new FrimpBody(tempBody);
            FragmentBuilder sootFragmentBuilder = new FragmentBuilder(
                    fb, (BlockGraph) graphtype.buildGraph(fb),
                    maxStructuresAllowed, minStructuresAllowed);
            sootFragmentBuilder.fillRepetitionCounts(repetitionCounts);            
        }
        
        Iterator<Map.Entry<String, IntegerHolder>> itAll  = repetitionCounts.entrySet().iterator();
        while(itAll.hasNext()){
            Map.Entry<String, IntegerHolder> entry = itAll.next();
            
            MTDFragmentAST tree = OBFragment.parseTree(entry.getKey());
            // process only if the tree has the expected size.
            if (tree.getSize() >= minStructuresAllowed
                    && tree.getSize() <= maxStructuresAllowed) {            
            output.write(entry.getValue().getValue() + "\t" + entry.getKey() + "\n");
            }
        }        
        output.close();
    }

    public static BodyStealer stealBodiesFromDir(String dir, String outputPath)
            throws FileNotFoundException, Exception {
        List < String > argumentos = new LinkedList < String >();

        argumentos.add("-allow-phantom-refs");
        // phantom refs
        argumentos.add("-p"); // for supporting phantom refs
        argumentos.add("jb.tr"); // for supporting phantom refs
        argumentos.add("enabled:false"); // for supporting phantom refs
        // phantom refs

        argumentos.add("--soot-class-path");
        String cp = dir + File.pathSeparator + System.getProperty("java.home")
                + File.separator + "lib" + File.separator + "rt.jar";
        logger.info("class path: " + cp);
        argumentos.add(cp);
        argumentos.add("-output-format");
        argumentos.add("n");
        argumentos.add("-output-dir");
        argumentos.add(System.getProperty("user.home") + File.separator
                + "temp" + File.separator + "sootemptyness");
        // argumentos.add("--soot-class-path");
        // argumentos.add(dir);
        // argumentos.add("-x");
        // argumentos.add("java*");

        // argumentos.add("-process-dir");
        // argumentos.add(dir);
        int size = argumentos.size();
        getClassFiles(new File(dir), argumentos, dir);
        if (size == argumentos.size()) {
            throw new Exception("Could not find any classes :(");
        }

        // G.v().reset(); // reset soot
        // G.reset(); // just in case.
        G.v().out = new PrintStream(outputPath + File.separator + "sootLog.txt");
        BodyStealer stealer = new BodyStealer();

        Transform stealerTransform = new Transform("jtp.stealerTransform",
                stealer);
        stealerTransform.setDeclaredOptions("enabled");
        stealerTransform.setDefaultOptions("enabled");
        PackManager.v().getPack("jtp").add(stealerTransform);

        String[] sootArgs = new String[argumentos.size()];
        argumentos.toArray(sootArgs);

        soot.Main.main(sootArgs);
        logger.info("Extracted soot methods for: " + dir);
        G.v().out.close();
        return stealer;

    }

    /**
     * Iterates the given directory, and returns all the .class files found in
     * it, if there are other directories. iterates through all of them
     * @param x
     *                the directory
     * @param output
     *                the output where class file names will be stored
     */
    protected static void getClassFiles(File x, List < String > output,
            String dir) {
        File[] files = x.listFiles();
        int i = 0;
        while (i < files.length) {
            if (files[i].isDirectory()) {
                getClassFiles(files[i], output, dir);
            } else if (files[i].getName().matches(".*[.]class$")) {
                String p = files[i].getParent().replace(dir + File.separator,
                        "").replaceAll(File.separator, ".");
                String c = p + "."
                        + files[i].getName().replaceFirst("[.]class$", "");
                // logger.info("processing class:" + c);
                output.add(c);
            }
            i++;
        }
    }

}
