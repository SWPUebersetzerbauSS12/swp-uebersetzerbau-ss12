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

package tokenmatcher;



public interface DeterministicFiniteAutomata<E,Payload> {
	
	
	
	/**
	 * Wechselt in einen anderen Zustand, sofern 
	 * es einen Übergang in der Übergangstabelle ausgehend vom aktuellen Zustand in 
	 * den angegebenen Element gibt. 
	 * 
	 * @param element Das Element, welches den Übergang definiert.
	 * @return 	Den neuen Zustand oder null, falls es keinen 
	 * 					Übergang in der Übergangstabelle gibt.
	 */
	State<Payload> changeStateByElement( E element);
	
	
	
	/**
	 * Prüft, ob ein Übergang für das Lesen des 
	 * spezifizierten Elementes definiert ist. 
	 * @param element Das Element, für das geprüft werden soll, ob ein Übergang 
	 *               aus dem aktuellen Zustand durch Lesen des Elementes definiert ist.
	 * @return true, wenn es einen Übergang gibt, anderenfalls false.
	 */
	boolean canChangeStateByElement( E element);
	
	
	/**
	 * @return Liefert den aktuellen Zustand.
	 */
	State<Payload> getCurrentState();
	
	
	/**
	 * Setzt den DFA wieder in den Startzustand zurück.
	 */
	void resetToInitialState();
	
	
}
