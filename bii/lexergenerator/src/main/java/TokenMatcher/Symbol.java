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
 * Authors: Johannes Dahlke
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package TokenMatcher;

/**
 * Ein Symbol vereint eine Sequenz an Zeichen, mit der in der 
 * Übergangstabelle  Zustandsübergange definiert werden.
 * 
 * @author Johannes Dahlke
 *
 */
public class Symbol {
	
	/**
	 *  Die Zeichensequenz, welche in dieser Reihenfolge
	 *  von dem einen in den nächsten Zustand fürt. In der Regel 
	 *  ist das immer nur ein Zeichen. Aber durch den String 
	 *  lässt sich eine nichtverzweigte Zustandsequenz ersetzen.  
	 */
	public String characterSequence; 

	
	/**
	 * Die Gleichheit eines Symbol leitet sich von der Gleichheit der Zeichensequenz ab, 
	 * welche das Symbol repräsentiert.
	 */
	@Override
	public boolean equals( Object theOtherObject) {
		if ( theOtherObject instanceof Symbol)
			return ((Symbol) theOtherObject).characterSequence.equals( characterSequence); 
		return false;
	}
}
