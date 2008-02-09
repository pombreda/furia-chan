package org.kit.furia.fragment;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.kit.furia.exceptions.IRException;
import org.kit.furia.fragment.soot.NoClassesFound;
import org.kit.furia.fragment.soot.NoClassesFoundByStealer;

import soot.util.cfgcmd.CFGGraphType;

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
 * FragmentExtractors take a directory with class files and extract all the
 * fragments from it.
 * @author Arnoldo Jose Muller Molina
 */

public interface FragmentExtractor {

           
    
    /**
     * Extracts fragments from the given directory.
     * Furia-chan's fragment file format is:
     * <repetitions count>\t<fragment>\n
     * where <repetitions count> is a string representation of an integer
     * and <fragment> is a string representation of a tree (for example: a(b,c))
     * @param directory Directory from where we will extract fragments.
     * @param maxStructuresAllowed  Maximum nodes per tree.
     * @param minStructuresAllowed Minimum nodes per tree.
     * @param outputPath Output path where logs will be written.
     * @param outputFile The file where the fragments will be stored.
     * @throws FileNotFoundException If the input or output files cannot be found.
     * @throws NoClassesFound If no classes were found. 
     * @throws IOException If an IO error occurs.
     * @throws IRException If some other error occurs it will be wrapped on this exception.
     * @throws FragmentParseException If a fragment is not generated properly, this exception will be thrown. For debugging purposes.
     */
    public  void extractMethodsFromDirectory(
            final String directory,
            final int maxStructuresAllowed, final int minStructuresAllowed,
            final String outputPath, String outputFile) throws
            FileNotFoundException, NoClassesFound, IOException, IRException, FragmentParseException;

}
