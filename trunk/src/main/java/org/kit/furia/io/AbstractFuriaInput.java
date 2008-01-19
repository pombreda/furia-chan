package org.kit.furia.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.ajmm.obsearch.OB;
import org.ajmm.obsearch.exception.OBException;
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
    public Iterator < Document < O >> getDocumentsFromDirectory(File directory)
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
                O word = readObjectFromStringLine(re);
                doc.addWord(word);
            }
            re = r.readLine();
        }
        return doc;
    }

    /**
     * Returns true if the given line is not null or if it is not a comment.
     * @return true if the given line can be parsed.
     */
    public boolean isParsableLine(final String line) {
        return "".equals(line.trim()) || (line.startsWith("#"));
    }

    private class FuriaInputIterator implements Iterator < Document < O >> {
        /**
         * The documents that will be lazily processed.
         */
        private File[] documents;

        /**
         * The current index that will be processed.
         */
        private int i;

        FuriaInputIterator(File directory) {
            documents = directory.listFiles();
            i = 0;
            moveTapeToNextValidDocument();
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
                moveTapeToNextValidDocument();
            } catch (Exception e) {
                throw new NoSuchElementException(res.toString());
            }
            return res;
        }

        /**
         * The remove operation does not make sense in this Iterator. This
         * method does not do anything.
         */
        public void remove() {

        }

        public boolean hasNext() {
            return i < documents.length;
        }
    }
}
