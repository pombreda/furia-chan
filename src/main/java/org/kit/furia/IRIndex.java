package org.kit.furia;

import org.ajmm.obsearch.OB;
import java.util.List;
import org.ajmm.obsearch.Index;

/*
 OBSearch: a distributed similarity search engine
 This project is to similarity search what 'bit-torrent' is to downloads.
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
 * This interface holds documents composed of OB objects
 * (http://obsearch.berlios.de/). 
 * An IRIndex takes advantage of the
 * distribution of these objects, to match groups of objects. If each object is
 * a word (natural language) then, what we do here is what any information
 * retrieval does when you "click" on "find similar pages".
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */
public interface IRIndex < O extends OB > {

    void insert(List < O > objects, String documentName);

    void delete(String documentName);

    Index < O > getIndex();

}
