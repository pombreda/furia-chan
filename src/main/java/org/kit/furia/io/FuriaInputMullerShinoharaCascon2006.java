package org.kit.furia.io;

import org.ajmm.obsearch.OB;
import org.ajmm.obsearch.exception.OBException;
import org.kit.furia.fragment.FragmentMullerShinoharaCascon2006;

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
 * FuriaInputMullerShinoharaCascon2006 creates documents of type
 * {@link org.kit.furia.fragment.FragmentMullerShinoharaCascon2006()}
 * from folders or files.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */
public class FuriaInputMullerShinoharaCascon2006
        extends AbstractFuriaInput < FragmentMullerShinoharaCascon2006 > {

    @Override
    protected FragmentMullerShinoharaCascon2006 readObjectFromStringLine(
            String data) throws OBException {
        return new FragmentMullerShinoharaCascon2006(data);
    }

}
