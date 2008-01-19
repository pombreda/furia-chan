package org.kit.furia.fragment;

import org.ajmm.obsearch.exception.OBException;

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
 * Class used to generate an error when a Tree cannot be parsed properly.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */

public class FragmentParseException
        extends OBException {

    /**
     * Serial version of the class.
     */
    private static final long serialVersionUID = 3774865697155505953L;

    /**
     * Constructor.
     * @param x
     *                Original slice
     * @param e
     *                Exception that was thrown
     */
    FragmentParseException(final String x, final Exception e) {
        super(x, e);
    }

}
