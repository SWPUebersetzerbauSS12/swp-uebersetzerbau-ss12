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

import java.util.HashMap;
import java.util.Set;


public class TransitionTable {
	
	private HashMap<State,HashMap<Symbol,State>> transitions = new HashMap<State, HashMap<Symbol,State>>();  
	
	public TransitionTable( Set<Symbol> symbols) {
		super();
	}
	
	/**
	 * Erzeugt einen Übergang vom fromState zum toState über das symbol.
	 * Wenn es bereits ein Übergang in der Übergangstabelle für fromState 
	 * beim Lesen von symbol definiert wurde, dann wird der Übergang mit 
	 * dem neuen toState überschrieben.
	 * 
	 * @param fromState
	 * @param toState
	 * @param symbol
	 */
	public void enterTransition( State fromState, State toState, Symbol symbol) {
		if ( !transitions.containsKey( fromState)) 
			transitions.put( fromState, new HashMap<Symbol, State>());
		transitions.get( fromState).put( symbol, toState);	
	}
	
	

	public boolean hasTransition( State fromState, State toState) {
		if ( transitions.containsKey( fromState))
			for ( Symbol symbol : transitions.get( fromState).keySet()) {
				if ( transitions.get( fromState).get( symbol).equals( toState))
					return true;
			}
		return false;
	}

	public Symbol getSymbolOfTransition( State fromState, State toState) {
		try {
			for ( Symbol symbol : transitions.get( fromState).keySet()) {
				if ( transitions.get( fromState).get( symbol).equals( toState))
					return symbol;
			}
		} catch( Exception e) {}
		return null;
		// throw new Exception( "No transition defined between the given states.");
	}

	public boolean hasTransitionForSymbol( State fromState, Symbol symbol) {
		return transitions.containsKey( fromState) && 
  			   transitions.get( fromState).containsKey( symbol);
	}

	public State getNewStateOfTransitionForSymbol( State fromState, Symbol symbol) {
		try {
			return transitions.get( fromState).get( symbol);	
		} catch( Exception e) {	
			return null;
			// throw new Exception( "No transition defined between the given states.");		
		}
	}
	
}
