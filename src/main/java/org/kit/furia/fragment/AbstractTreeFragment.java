package org.kit.furia.fragment;

import java.io.StringReader;

import org.ajmm.obsearch.OB;
import org.ajmm.obsearch.exception.OBException;
import org.ajmm.obsearch.ob.OBShort;

import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

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
 * AbstractTreeFragment abstracts functionality required by fragment objects
 * that are based on trees.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */
public class AbstractTreeFragment implements OB {

    /**
     * The root node of the tree.
     */
    protected FragmentAST tree;

    private int hashCode;

    /**
     * Default constructor required by OBSearch.
     */
    public AbstractTreeFragment() {
        super();
    }

    /**
     * Creates a fragment from the given string.
     * @param fragment
     *                A string representation of a tree.
     */
    public AbstractTreeFragment(final String fragment) throws OBException {
        this.updateTree(fragment);
        assert tree.equalsTree(this.parseTree(tree.toFuriaChanTree())) : "This: "
                + tree.toFuriaChanTree()
                + " slice: "
                + fragment
                + " size: "
                + tree.getSize();
    }

    /**
     * Internal method that updates the Tree from the String
     * @throws OBException
     */
    protected final void updateTree(String x) throws OBException {
        tree = parseTree(x);
        this.hashCode = tree.toFuriaChanTree().hashCode();
    }

    protected final FragmentAST parseTree(String x)
            throws FragmentParseException {
        try {
            FragmentLexer lexer = new FragmentLexer(new StringReader(x));
            FragmentParser parser = new FragmentParser(lexer);
            parser.setASTNodeClass("org.kit.furia.fragment.FragmentAST");
            parser.fragment();
            FragmentAST t = (FragmentAST) parser.getAST();
            t.updateDecendantInformation();
            return t;
        } catch (Exception e) {
            throw new FragmentParseException(x, e);
        }
    }

    /**
     * Returns the size (in nodes) of the tree.
     * @return The size of the tree.
     * @throws OBException
     *                 If something goes wrong.
     */
    public final int size() throws OBException {
        return tree.getSize();
    }

    /**
     * @return A String representation of the tree.
     */
    public final String toString() {
        String res = ":)";
        try {
            res = tree.toFuriaChanTree() + "|" + tree.getSize() + "|";
        } catch (Exception e) {
            assert false;
        }
        return res;
    }

    /**
     * Re-creates this object from the given byte stream
     * @param in
     *                A byte stream with the data that must be loaded.
     * @see org.ajmm.obsearch.Storable#load(com.sleepycat.bind.tuple.TupleInput)
     */
    public final void load(TupleInput in) throws OBException {
        short size = in.readShort();
        updateTree(in.readBytes(size));
    }

    /**
     * Stores this object into the given byte stream.
     * @param out
     *                The byte stream to be used
     * @see org.ajmm.obsearch.Storable#store(com.sleepycat.bind.tuple.TupleOutput)
     */
    public final void store(TupleOutput out) {
        String str = tree.toFuriaChanTree();
        out.writeShort(str.length());
        out.writeBytes(str);
    }

    /**
     * Returns true of this.tree.equals(obj.tree). For this distance function
     * this.distance(obj) == 0 implies that this.equals(obj) == true
     * @param obj
     *                Object to compare.
     * @return true if this == obj
     */
    public final boolean equals(final Object obj) {
        if (!(obj instanceof FragmentMullerShinoharaCascon2006)) {
            assert false;
            return false;
        }
        FragmentMullerShinoharaCascon2006 o = (FragmentMullerShinoharaCascon2006) obj;
        return tree.equalsTree(o.tree);
    }

    /**
     * A hashCode based on the string representation of the tree.
     * @return a hash code of the string representation of this tree.
     */
    public final int hashCode() {
        return this.hashCode;
    }

}
