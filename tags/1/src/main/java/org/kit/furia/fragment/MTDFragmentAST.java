package org.kit.furia.fragment;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.kit.furia.misc.IntegerHolder;

import antlr.collections.AST;

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
 * MTDFragmentAST A tree that holds an internal id for each unique complete
 * subtree and a hash code computed on the string representation of this
 * complete subtree. Additionally, the number of repetitions is included. This
 * helps to make this algorithm O(n) for equal complete subtrees of two
 * different trees. Once we found that two complete subtrees m,j belonging to
 * different trees T1 T2, we can compute their intersection in linear time.
 * @author Arnoldo Jose Muller Molina
 */
public class MTDFragmentAST
        extends FragmentAST {

    int hashCode = -1;

    public int id = -1;

    public IntegerHolder repetitions;

    public void update() {
        super.updateDecendantInformation();
        hashCode = super.toStringTree().hashCode();
    }

    public boolean equals(AST x) {
        return equals((Object) x);
    }

    public int hashCode() {
        return hashCode;
    }

    public boolean equals(Object o) {
        MTDFragmentAST other = (MTDFragmentAST) o;
        boolean res = fequalsTree(other);
        assert res == this.toStringTree().equals(other.toStringTree());
        return res;
    }

    private boolean fequalsTree(MTDFragmentAST other) {

        if (other == null 
                || this.hashCode != other.hashCode
                || this.decendants != other.decendants
                || !this.text.equals(other.text)) {
            return false;
        }

        if (this.getLeft() != null) {
            return this.getLeft().fequalsTreeAux(other.getLeft());
        } else if (this.getLeft() == null && other.getLeft() == null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean fequalsTreeAux(MTDFragmentAST other) {
        if (other == null) {
            return false;
        }
        if (!this.text.equals(other.text)) {
            return false;
        }

        boolean res = true;
        MTDFragmentAST left = this.getLeft();
        if (left != null) {
            res = left.fequalsTreeAux(other.getLeft());
        }
        if (res) {
            MTDFragmentAST sib = this.getSibbling();
            if (sib != null) {
                res = sib.fequalsTreeAux(other.getSibbling());
            }
        }
        return res;
    }

    public MTDFragmentAST getLeft() {
        return (MTDFragmentAST) this.getFirstChild();
    }

    public MTDFragmentAST getSibbling() {
        return (MTDFragmentAST) this.getNextSibling();
    }

    

}
