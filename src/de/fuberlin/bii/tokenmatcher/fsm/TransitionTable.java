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

package de.fuberlin.bii.tokenmatcher.fsm;

import java.util.HashMap;

import de.fuberlin.bii.utils.Test;



/**
 * 
 * @author Johannes Dahlke
 *
 * @param <E>
 * @deprecated
 */
public class TransitionTable<E> {
	
	private HashMap<State,HashMap<E,State>> transitions = new HashMap<State, HashMap<E,State>>();  
	
	private static State startState = null;
	
	public TransitionTable() {
		super();
	}
	
	/**
	 * Erzeugt einen Übergang vom fromState zum toState über das element.
	 * Wenn es bereits ein Übergang in der Übergangstabelle für fromState 
	 * beim Lesen von element definiert wurde, dann wird der Übergang mit 
	 * dem neuen toState überschrieben.
	 * 
	 * @param fromState
	 * @param toState
	 * @param element
	 */
	public void enterTransition( State fromState, State toState, E element) {
		// check for correct use of start state
		checkAndEnsureUniqueStartState( toState);
		checkAndEnsureUniqueStartState( fromState);
		// insert transition into table 
		if ( !transitions.containsKey( fromState)) 
			transitions.put( fromState, new HashMap<E, State>());
		transitions.get( fromState).put( element, toState);	
	}
	
	

	private static void checkAndEnsureUniqueStartState( State theState) {
		if ( theState.isStartState()) {
			if ( Test.isAssigned( startState)) {
			  startState.unsetStart();
			  startState = theState;
		  }
		}
	}

	public boolean hasTransition( State fromState, State toState) {
		if ( transitions.containsKey( fromState))
			for ( E element : transitions.get( fromState).keySet()) {
				if ( transitions.get( fromState).get( element).equals( toState))
					return true;
			}
		return false;
	}

	public E getElementOfTransition( State fromState, State toState) {
		try {
			for ( E element : transitions.get( fromState).keySet()) {
				if ( transitions.get( fromState).get( element).equals( toState))
					return element;
			}
		} catch( Exception e) {}
		return null;
		// throw new Exception( "No transition defined between the given states.");
	}

	public boolean hasTransitionForElement( State fromState, E element) {
		return transitions.containsKey( fromState) && 
  			   transitions.get( fromState).containsKey( element);
	}

	public State getNewStateOfTransitionForElement( State fromState, E element) {
		try {
			return transitions.get( fromState).get( element);	
		} catch( Exception e) {	
			return null;
			// throw new Exception( "No transition defined between the given states.");		
		}
	}
	
	
	public static State getStartState() {
		return startState;
	}
	
}
