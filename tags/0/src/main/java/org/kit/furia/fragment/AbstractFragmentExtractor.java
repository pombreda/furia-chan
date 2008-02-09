package org.kit.furia.fragment;

import java.io.File;
import java.util.List;


public abstract class AbstractFragmentExtractor implements FragmentExtractor {

    public AbstractFragmentExtractor() {
        super();
    }
    
    /**
     * Receives a directory and returns all the class files found
     * in it.
     * @param directory
     * @param result Returns all the files in the given folder
     */
    protected void getClassFiles(File directory, List<File> result) {
        File[] files = directory.listFiles();
        int i = 0;
        while (i < files.length) {
            if (files[i].isDirectory()) {
                getClassFiles(files[i], result);
            } else if (files[i].getName().matches(".*[.]class$")) {
               result.add(files[i]);
            }
            i++;
        }
    }

    /**
     * Iterates the given directory, and returns all the .class files found in
     * it, if there are other directories. iterates through all of them
     * @param x
     *                the directory where we will take the classes from
     * @param output
     *                A list where all the file names will be stored.
     * @param dir The parent class file.
     *  
     */
    protected void getClassFiles(File x, List < String > output, String dir) {
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