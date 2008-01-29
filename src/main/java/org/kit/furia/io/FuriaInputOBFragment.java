package org.kit.furia.io;

import java.io.File;

import org.ajmm.obsearch.OB;
import org.ajmm.obsearch.exception.OBException;
import org.kit.furia.fragment.MTDFragmentAST;
import org.kit.furia.fragment.OBFragment;

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
 * This class loads objects of type FuriaInputOBFragment.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */
public class FuriaInputOBFragment
        extends AbstractFuriaInput < OBFragment > {
    
    public FuriaInputOBFragment(File directory){
        super(directory);
    }
    
    @Override
    protected OBFragment readObjectFromStringLine(
            String data) throws OBException {
        return new OBFragment(data);
    }

}
