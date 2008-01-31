package org.kit.furia;

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
 * FuriaChanConstants contains constants used by all the other programs.
 * @author Arnoldo Jose Muller Molina
 */

public class FuriaChanConstants {

    /**
     * Maximum number of nodes to be accepted per fragment.
     * Warning: This value must be observed by all the fragment engines.
     */
    public static final int MAX_NODES_PER_FRAGMENT = 1000;

    /**
     * Minimum number of nodes to be accepted per fragment.
     * Warning: This value must be observed by all the fragment engines.
     */
    public static final int MIN_NODES_PER_FRAGMENT = 1;

    /**
     * Minimum number of different fragments that a program must hold to be
     * retrieved successfully.
     */
    public static int MIN_DOC_SIZE = 100;

}
