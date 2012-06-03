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


package regextodfaconverter.directconverter.lr0parser;

import java.util.List;
import java.util.Queue;
import java.util.Stack;

import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;

public interface ItemAutomata<Element extends Comparable<Element>> {

	boolean match( List<Element> input);
		
	/**
	 * - Keine shift-reduce Konflikte
	 * - Keine reduce-reduce Konflikte
	 * - reduce-Aktionen über Followmengen plaziert
	 * @return
	 */
  boolean isSLR1();
  
	
  void setReduceEventHandler( ReduceEventHandler reduceEventHandler);
	
	
	void setShiftEventHandler( ShiftEventHandler shiftEventHandler);
	
}
