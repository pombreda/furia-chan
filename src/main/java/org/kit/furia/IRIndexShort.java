package org.kit.furia;

import org.ajmm.obsearch.ob.OBShort;
import java.util.List;

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
	*  Class: IRIndexShort
	*  
  *  @author      Arnoldo Jose Muller Molina    
  *  @since       0
  */
public interface IRIndexShort<O extends OBShort> extends IRIndex<O> {

		List<Result> search(List<O> objects, byte k, short r);
		
}
