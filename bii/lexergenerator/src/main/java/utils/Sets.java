/*
 * 
 * Copyright 2012 lexergen.
 * This file is part of lexergen.
 * 
 * lexergen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * lexergen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with lexergen.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * lexergen:
 * A tool to chunk source code into tokens for further processing in a compiler chain.
 * 
 * Projectgroup: bi, bii
 * 
 * Authors: Benjamin Weißenfels
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;


/**
 * 
 * @author Johannes Dahlke
 * 
 */
public class Sets {

	
	/**
	 * Merge to collections to a union.
	 * @param c1
	 * @param c2
	 * @return a HashSet with all elements of both lists once.
	 */
	public static <T> Collection<T> unionCollections( Collection<T> c1,
			Collection<T> c2) {

		Collection<T> list = new HashSet<T>();

		if ( Test.isAssigned( c1) && Test.isAssigned( c2)) {
			list.addAll( c1);

			for ( T t : c2) {
				if ( !list.contains( t))
					list.add( t);
			}
		} else if ( Test.isAssigned( c1)) {
			list.addAll( c1);
		} else if ( Test.isAssigned( c2)) {
			list.addAll( c2);
		}
		return list;
	}

}
