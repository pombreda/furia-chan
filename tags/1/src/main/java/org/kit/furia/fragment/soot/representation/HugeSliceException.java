package org.kit.furia.fragment.soot.representation;
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
	*  HugeSliceException When a fragment becomes a huge tree, we have no other
  *  choice than to release this exception. This is because it will impose a big
  *  burden on OBSearch.
	*  
  *  @author      Arnoldo Jose Muller Molina    
  */

public class HugeSliceException extends Exception {

}
