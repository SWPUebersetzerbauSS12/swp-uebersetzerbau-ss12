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

package tokenmatcher.fsm;

import tokenmatcher.State;
import utils.Test;


/**
 * 
 * @author Joihannes Dahlke
 *
 * @param <E>
 */
public class FiniteStateMachine<E> {
	
	private State currentState;
	
	
	private TransitionTable<E> transitionTable = null;
	
	
	
	public FiniteStateMachine( TransitionTable<E> transitionTable) throws Exception {
		super();
		if ( Test.isUnassigned( transitionTable))
			throw new Exception( "No transition table forwarded.");
		this.transitionTable = transitionTable;
		this.currentState = transitionTable.getStartState();
	}
	
	/*
	public FiniteStateMachine() {
		super();
		this.transitionTable = new TransitionTable();
	}
	*/
	
	
	/**
	 * Wechselt in einen anderen Zustand, sofern 
	 * es einen Übergang in der Übergangstabelle zu 
	 * dem angegebenen Element gibt. 
	 * 
	 * @param element Das Element, welches den Übergang definiert.
	 * @return 	Den neuen Zustand oder null, falls es keinen 
	 * 					Übergang in der Übergangstabelle gibt.
	 */
	public State changeStateByElement( E element) {
		if ( canChangeStateByElement( element))
			return transitionTable.getNewStateOfTransitionForElement( currentState, element);
		else
		  return null;
	}
	
	
	/**
	 * Wechselt in einen gegebenen Zustand, sofern 
	 * es einen Eintrag zwischen dem aktuellen Zustand 
	 * und dem angegebenen Zustand gibt.   
	 * @param newState Der gewünschte neue Zustand
	 * @return Das Element , welches für den Übergangswechsel 
	 * 					"gelesen" wurde, oder null, falls es keinen 
	 * 					Übergang und damit auch keinen Zustandswechel gab.  
	 */
	public E changeToState( State newState) {
		if ( transitionTable.hasTransition( currentState, newState))
			return transitionTable.getElementOfTransition( currentState, newState); 
		else 
			return null;
	}
	
	/**
	 * Prüft, ob ein Übergang für das Lesen des 
	 * spezifizierten Elementes definiert ist. 
	 * @param element Das Element, für das geprüft werden soll, ob ein Übergang 
	 *               aus dem aktuellen Zustand durch Lesen des Elementes definiert ist.
	 * @return true, wenn es einen Übergang gibt, anderenfalls false.
	 */
	public boolean canChangeStateByElement( E element) {
		return transitionTable.hasTransitionForElement( currentState, element);
	}
	
	
	/**
	 * @return Liefert den aktuellen Zustand.
	 */
	public State getCurrentState() {
		return currentState;
	}
	
	
	
	
	
	
	

}
