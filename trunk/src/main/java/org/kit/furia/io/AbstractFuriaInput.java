package org.kit.furia.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.ajmm.obsearch.OB;
import org.ajmm.obsearch.exception.OBException;
import org.apache.log4j.Logger;
import org.kit.furia.Document;

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
 * AbstractFuriaInput is in charge of reading fragment files and creating
 * documents out of them.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */

public abstract class AbstractFuriaInput < O extends OB > {

    private static final Logger logger = Logger.getLogger(AbstractFuriaInput.class);

    private File directory;
    
    /**
     * Creates a new fragment file reader based on the given directory.
     * @param directory
     */
    public AbstractFuriaInput(File directory){
        this.directory = directory;
    }
    
    /**
     * Reads and creates an O object from the given string.
     * @param data
     *                The string to be parsed
     * @return an O object that was created from data
     * @throws OBException if something goes wrong when parsing the data.
     */
    protected abstract O readObjectFromStringLine(String data) throws OBException;

    /**
     * The name of the file that holds the fragments (words) inside a directory.
     */
    public static final String fragmentFileName = "fragments";

    /**
     * This method receives a directory and returns an iterator that will lazily
     * create documents from the given directory. The directory is composed of
     * directories in which a file called "fragments" was previously created.
     * @param directory
     *                that will be processed
     * @throws IOException
     *                 If the given directory does not exist.
     * @return An iterator that will return one by one all the documents found
     *         in the given directory.
     */
    public Iterator < Document < O >> getDocumentsFromDirectory()
            throws IOException {
        if (!directory.exists()) {
            throw new IOException("Directory does not exist: " + directory);
        }
        return new FuriaInputIterator(directory);
    }

    /**
     * Parses a file that is in the furia-chan fragment file format: "#" starts
     * a comment and it is ignored. Every object is a string separated by a
     * newline. The subclass knows how to interpret this line, and an
     * appropriate O object will be generated from this line.
     * @param fragments
     *                A file in which fragment files can be found.
     * @param id
     *                The id that the document will hold.
     * @return A document of O objects created from the given file.
     * @throws IOException
     *                 If fragments does not exist, or any other error occurs.
     */
    public Document < O > getDocument(String id, File fragments)
            throws IOException, OBException {
        if (!fragments.exists()) {
            throw new IOException("File does not exist: " + fragments);
        }
        Document < O > doc = new Document < O >(id);
        BufferedReader r = new BufferedReader(new FileReader(fragments));
        String re = r.readLine();
        while (re != null) {
            if (isParsableLine(re)) {
                String[] tuple = re.split("\t");
                int multiplicity = Integer.parseInt(tuple[0]);
                O word = readObjectFromStringLine(tuple[1]);
                doc.setWord(word,multiplicity);
            }
            re = r.readLine();
        }
        r.close();
        return doc;
    }

    /**
     * Returns true if the given line is not null or if it is not a comment.
     * @return true if the given line can be parsed.
     */
    public boolean isParsableLine(final String line) {
        return  !("".equals(line.trim()) || (line.startsWith("#")));
    }

    /**
     * Iterator class that creates Documents from the given directory.
     * @author Arnoldo Jose Muller Molina
     *
     */
    private class FuriaInputIterator implements Iterator < Document < O >> {
        
        private class FragmentsFileFilter implements FilenameFilter{

            public boolean accept(File dir, String name) {
                return name.equals(fragmentFileName);
            }            
        }
        
        private FragmentsFileFilter  fileFilter = new FragmentsFileFilter();
        
        /**
         * The documents that will be lazily processed.
         */
        private File[] documents;

        /**
         * The current index that will be processed.
         */
        private int i;

        /**
         * Builds an iterator of applications. If the given directory
         * has a "fragments" file, then the program works in Single app mode.
         * Otherwise, we run in directory of applications mode.
         * @param directory
         */
        FuriaInputIterator(File directory) {
            if(directoryOfDirectoriesMode(directory)){
                documents = directory.listFiles();
            }else{
                File[] documents = new File[1];
                documents[0] = directory;
            }
           
            i = 0;
            moveTapeToNextValidDocument();
        }
        
        /**
         * Returns true if the given directory does not have a fragments file.
         * This means that we will operate on a directory of directories.
         * @param directory
         * @return
         */
        private boolean directoryOfDirectoriesMode(File directory){
            File [] all = directory.listFiles(fileFilter);            
            return all.length !=1;
        }

        /**
         * Moves to the next valid document.
         */
        private void moveTapeToNextValidDocument() {
            while (i < documents.length) {
                if (documents[i].isDirectory()) {
                    File data = new File(documents[i], fragmentFileName);
                    if (data.exists()) {
                        break;
                    }
                }
                i++;
            }
        }

        /**
         * Returns the next document.
         * @return The next document.
         * @throws NoSuchElementException
         *                 if {@link #hasNext()} == false or if the current
         *                 element could not be processed.
         */
        public Document < O > next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements!");
            }
            File data = new File(documents[i], fragmentFileName);
            String name = documents[i].getName();
            Document < O > res = null;
            try {
                res = getDocument(name, data);
                i++;
                moveTapeToNextValidDocument();
            } catch (Exception e) {
                if(logger.isDebugEnabled()){
                    logger.debug(e);
                }
                throw new NoSuchElementException(res.toString());
            }
            return res;
        }

        /**
         * The remove operation does not make sense in this Iterator. This
         * method does not do anything.
         */
        public void remove() {
            assert false;
        }

        public boolean hasNext() {
            return i < documents.length;
        }
    }
}
