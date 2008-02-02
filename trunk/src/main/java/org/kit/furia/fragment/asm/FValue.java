package org.kit.furia.fragment.asm;

import java.util.Set;

import org.kit.furia.fragment.soot.HugeFragmentException;
import org.kit.furia.misc.IntegerHolder;
import org.objectweb.asm.tree.analysis.Value;

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
 * FValue defines additional properties than the properties defined by ASM's
 * value required by Furia.
 * @author Arnoldo Jose Muller Molina
 */
public interface FValue extends Value {
    /**
     * This function returns the string representation of the given expression.
     * @param result The resulting string representation of the given expression.
     * @param visited A set used to control recursive references.
     * @param count The number of "expansions" that have been executed.
     * @param max The maximum number of expansions that will be executed.
     */
     void toFragment(StringBuilder result, Set visited, IntegerHolder count, int max) throws HugeFragmentException;
}
