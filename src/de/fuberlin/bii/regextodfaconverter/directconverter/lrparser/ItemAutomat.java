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

package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser;

import java.util.List;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;

/**
 * Schnittstelle eines Item-Automaten.
 * 
 * @author Johannes Dahlke
 *
 * @param <Element>
 */
@SuppressWarnings("rawtypes")
public interface ItemAutomat<Element extends Symbol> {

	/**
	 * Prüft die Eingabe gegen die Grammatik. 
	 * 
	 * @return True, wenn die Eingabe konform zur Grammatik ist, sonst false.  
	 */
	boolean match( List<Element> input) throws ItemAutomatException;
		
	/**
	 * - Keine shift-reduce Konflikte
	 * - Keine reduce-reduce Konflikte
	 * - reduce-Aktionen über Followmengen plaziert
	 * @return
	 */
  boolean isReduceConflictFree();
  
	/**
	 * Setzt die Reduce-Ereignisbehandlungsroutine.
	 * @param reduceEventHandler
	 */
  void setReduceEventHandler( ReduceEventHandler reduceEventHandler);
	
	/**
	 * Setzt die Shift-Ereignisbehandlungsroutine.
	 * @param reduceEventHandler
	 */	
	void setShiftEventHandler( ShiftEventHandler shiftEventHandler);
	
}
