package org.kit.furia.fragment;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.ajmm.obsearch.OB;
import org.ajmm.obsearch.exception.OBException;
import org.ajmm.obsearch.ob.OBShort;

import antlr.RecognitionException;


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
 * This class reads strings representations of trees and calculates the distance
 * between the objects by using a tree distance function. These trees are called
 * "program fragments". We use the distance function described in the following
 * papers:
 * 
 * <pre>
 *  A. Muller and T. Shinohara. On approximate matching of
 *  programs for protecting libre software. In CASCON 2006:
 *  Proceedings of the 2006 conference of the Center for Advanced
 *  Studies on Collaborative Research, pages 275-289,
 *  New York, NY, USA, 2006. ACM Press.
 * </pre>
 * <pre>
 *  A. Muller and T. Shinohara. Fast Approximate Matching of Programs 
 *  for Protecting Libre/Open Source Software by Using Spatial Indexes. 
 *  Seventh IEEE International Working Conference on Source Code Analysis
 *   and Manipulation (SCAM 2007), pages 111-120,
 *  Piscataway, NJ, USA, 2007. IEEE CPS.
 * </pre>
 * 
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */

public class FragmentMullerShinoharaCascon2006 extends AbstractTreeFragment implements OBShort {

    /**
     * Default constructor must be provided by every object that implements the
     * interface OB.
     */
    public FragmentMullerShinoharaCascon2006() {

    }

    /**
     * Creates a fragment from the given string.
     * @param fragment
     *                A string representation of a tree.
     */
    public FragmentMullerShinoharaCascon2006(final String fragment) throws OBException {
        super(fragment);
    }

    /**
     * Calculates the distance between two trees. TODO: traverse the smallest
     * tree.
     * @param object
     *                The other object to compare
     * @see org.ajmm.obsearch.OB#distance(org.ajmm.obsearch.OB,
     *      org.ajmm.obsearch.Dim)
     * @throws OBException
     *                 if something wrong happens.
     * @return A short that indicates how similar or different the trees are.
     */
    public final short distance(final OB object) throws OBException {
        FragmentMullerShinoharaCascon2006 b = (FragmentMullerShinoharaCascon2006) object;
        if (this.tree.getSize() < b.tree.getSize()) {
            return distance(this.tree, b.tree);
        } else {
            return distance(b.tree, this.tree);
        }
    }

    /**
     * Calculates the distance between trees a and b.
     * @param a
     *                The first tree (should be smaller than b)
     * @param b
     *                The second tree
     * @return The distance of the trees.
     */
    private final short distance(FragmentAST a, FragmentAST b) {

        List < FragmentAST > aExpanded = a.depthFirst();
        List < FragmentAST > bExpanded = b.depthFirst();
        List < FragmentAST > bExpanded2 = new LinkedList < FragmentAST >();
        bExpanded2.addAll(bExpanded);
        int Na = aExpanded.size() * 2;
        int Nb = bExpanded.size() * 2;

        ListIterator < FragmentAST > ait = aExpanded.listIterator();
        int res = 0;
        while (ait.hasNext()) {
            FragmentAST aTree = ait.next();
            ListIterator < FragmentAST > bit = bExpanded.listIterator();
            while (bit.hasNext()) {
                FragmentAST bTree = bit.next();
                if (aTree.equalsTree(bTree)) {
                    res++;
                    bit.remove();
                    break;
                }
            }
            // do the same for the nodes without children
            bit = bExpanded2.listIterator();
            while (bit.hasNext()) {
                FragmentAST bTree = bit.next();
                if (aTree.getText().equals(bTree.getText())) {
                    res++;
                    bit.remove();
                    break;
                }
            }
        }
        // return Na - res + Nb - res;
        // return (Na + Nb) - ( 2 * res);
        short r = (short) (((Na + Nb) - (2 * res)) / 2);
        assert r >= 0;
        return r;
    }

}
