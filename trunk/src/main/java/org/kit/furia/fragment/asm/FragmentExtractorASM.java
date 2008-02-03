package org.kit.furia.fragment.asm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kit.furia.exceptions.IRException;
import org.kit.furia.fragment.AbstractFragmentExtractor;
import org.kit.furia.fragment.FragmentParseException;
import org.kit.furia.fragment.MTDFragmentAST;
import org.kit.furia.fragment.OBFragment;
import org.kit.furia.fragment.soot.HugeFragmentException;
import org.kit.furia.fragment.soot.NoClassesFound;
import org.kit.furia.misc.IntegerHolder;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

public class FragmentExtractorASM
        extends AbstractFragmentExtractor {
    
    private static final Logger logger = Logger.getLogger(FragmentExtractorASM.class);
    private int huge = 0;
    public void extractMethodsFromDirectory(final String directory,
            final int maxStructuresAllowed, final int minStructuresAllowed,
            final String outputPath, String outputFile)
            throws FileNotFoundException, NoClassesFound, IOException,
            IRException, FragmentParseException {

        List < File > classFiles = new LinkedList < File >();
        super.getClassFiles(new File(directory), classFiles);
        if (classFiles.size() == 0) {
            throw new NoClassesFound();
        }
        Iterator < File > it = classFiles.iterator();
        HashMap < String, IntegerHolder > fragments = new HashMap < String, IntegerHolder >(100000);
        int i = 0;
        while (it.hasNext()) {
            File f = it.next();
            System.out.println(i + " of " + classFiles.size() + " size: " + fragments.size() + " huge: " + huge);
            System.out.flush();
            //logger.info(i + " of " + classFiles.size() + " size: " + fragments.size());
            processClass(f, fragments, maxStructuresAllowed);
            i++;
        }
        System.out.println("finished");
        // now we just have to write the fragments down into the file.
        FileWriter output = new FileWriter(outputFile);
        for (Map.Entry < String, IntegerHolder > entry : fragments.entrySet()) {
            String treeStr = entry.getKey();
            MTDFragmentAST tree = OBFragment.parseTree(treeStr);
            // process only if the tree has the expected size.
            if (tree.getSize() >= minStructuresAllowed
                    && tree.getSize() <= maxStructuresAllowed) {
                output.write(entry.getValue().getValue() + "\t"
                        + entry.getKey() + "\n");
            }
        }
        output.close();
    }

    protected String toString(FValue x, int max) throws HugeFragmentException{
        StringBuilder treeTemp = new StringBuilder();
        Set visited = new HashSet();
        IntegerHolder count = new IntegerHolder(0);
        x.toFragment(treeTemp, visited, count, max);
        return treeTemp.toString();
    }

    protected void processClass(File f,
            HashMap < String, IntegerHolder > fragments, int max) throws IOException {
        ClassReader cr = new ClassReader(new BufferedInputStream(
                new FileInputStream(f)));
        ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.SKIP_DEBUG);
        List < MethodNode > methods = cn.methods;
        Set expanded = new HashSet();        
        for (MethodNode m : methods) {
            if (m.instructions.size() > 0) {
                Analyzer a = new Analyzer(new FragmentInterpreter());
                try {
                    a.analyze(cn.name, m);
                } catch (Exception ignored) {
                }
                if (a.getFrames() != null) {
                    for (Frame frame : a.getFrames()) {                        
                        if (frame != null) {
                            int i = 0;
                            while (i < frame.getLocals()) {
                                Value val = frame.getLocal(i);
                                
                                if (val instanceof FValue && ! expanded.contains(val)) {
                                    try{
                                    String fragment = toString(((FValue) val), max);
                                    
                                    IntegerHolder multiplicity = fragments
                                            .get(fragment);
                                    if (multiplicity == null) {
                                        multiplicity = new IntegerHolder(0);
                                        fragments.put(fragment, multiplicity);
                                    }
                                    multiplicity.inc();
                                    }catch(HugeFragmentException e){
                                        huge++;
                                    }
                                    expanded.add(val);
                                }
                                i++;
                            }
                        }
                    }
                }
            }
        }
    }

}
