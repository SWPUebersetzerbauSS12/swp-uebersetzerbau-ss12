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
 * Authors: Daniel Rotar
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package regextodfaconverter.fsm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import regextodfaconverter.fsm.excpetions.NullStateException;
import regextodfaconverter.fsm.excpetions.StateNotReachableException;
import regextodfaconverter.fsm.excpetions.TransitionAlreadyExistsException;


/**
 * Stellt einen endlichen Automaten (finite state machine, kurz FSM) dar.
 * 
 * @author Daniel Rotar
 * 
 * @param <TransitionConditionType>
 *          Der Typ der Bedingung f�r einen Zustand�bergang.
 * @param <StatePayloadType>
 *          Der Typ des Inhalts der Zust�nde.
 */
public class FiniteStateMachine<TransitionConditionType extends Comparable<TransitionConditionType>, StatePayloadType> {

	/**
	 * Der Startzustand dieses endlichen Automatens.
	 */
	private State<TransitionConditionType, StatePayloadType> _initialState;
	/**
	 * Der aktuelle Zustand, in dem sich dieser endlichen Automatens befindet.
	 */
	private State<TransitionConditionType, StatePayloadType> _currentState;
	/**
	 * Die HashMap die alle Zust�nde dieses endlichen Automaten, geordnet nach
	 * ihren eindetige ID enth�lt.
	 */
	private HashMap<UUID, State<TransitionConditionType, StatePayloadType>> _states;


	/**
	 * Gibt der Startzustand dieses endlichen Automatens.
	 * 
	 * @return Der Startzustand dieses endlichen Automatens.
	 */
	public State<TransitionConditionType, StatePayloadType> getInitialState() {
		return _initialState;
	}


	/**
	 * Gibt den aktuelle Zustand, in dem sich dieser endlichen Automatens befindet
	 * zur�ck.
	 * 
	 * @return Der aktuelle Zustand, in dem sich dieser endlichen Automatens
	 *         befindet.
	 */
	public State<TransitionConditionType, StatePayloadType> getCurrentState() {
		return _currentState;
	}


	/**
	 * Setzt den aktuelle Zustand, in dem sich dieser endlichen Automatens
	 * befindet fest.
	 * 
	 * @param state
	 *          Der neue Zustand, in dem sich dieser endlichen Automatens befinden
	 *          soll.
	 * @throws NullStateException
	 *           Wenn null als Parameter f�r den neuen Zustand �bergeben wird.
	 * @throws StateNotReachableException
	 *           Wenn der Zustand nicht erreichbar oder Teil des endlichen
	 *           Automats ist.
	 */
	public void setCurrentState(
			State<TransitionConditionType, StatePayloadType> state)
			throws NullStateException, StateNotReachableException {
		if ( state == null)
			throw new NullStateException();
		if ( !containsStateWithUUID( state.getUUID()))
			throw new StateNotReachableException();
		_currentState = state;
	}


	/**
	 * Gibt den Zustand zur�ck, der die angegebene eindeutige UUID hat.
	 * 
	 * @return Die eindeutige ID des Zustands, der zur�ckgegeben werden soll. Gibt
	 *         es keinen Zustand, der die angegebene ID enth�lt wird null
	 *         zur�ckgegeben.
	 */
	public State<TransitionConditionType, StatePayloadType> getStateByUUID(
			UUID uuid) {
		if ( _states.containsKey( uuid)) {
			return _states.get( uuid);
		} else {
			return null;
		}
	}


	/**
	 * Gibt an ob der endliche Automat einen Zustand mit der angegebenen
	 * eindeutigen UUID beinhaltet.
	 * 
	 * @param uuid
	 *          Die UUID nach der in diesem endliche Automaten gesucht werden
	 *          soll.
	 * @return true, wenn die UUID in dem endliche Automat vorhanden ist, sonst
	 *         false.
	 */
	public boolean containsStateWithUUID( UUID uuid) {
		return _states.containsKey( uuid);
	}


	/**
	 * Gibt an, ob es sich bei diesem endlichen Automaten um einen deterministisch
	 * endlichen Automaten (deterministic finite automaton, kurz DFA) handelt.
	 * 
	 * @return true, wenn es sich um einen deterministisch endlichen Automaten
	 *         (deterministic finite automaton, kurz DFA) handelt, sonst false.
	 */
	public boolean isDeterministic() {
		for ( State<TransitionConditionType, StatePayloadType> state : _states
				.values()) {
			HashSet<TransitionConditionType> conditions = new HashSet<TransitionConditionType>();
			for ( Transition<TransitionConditionType, StatePayloadType> transition : state
					.getTransitions()) {
				if ( transition.getCondition() == null)
					return false; // Epsilon-�bergang vorhanden
				if ( !conditions.add( transition.getCondition()))
					return false; // �bergangsbedingung mehrfach vorhanden.
			}
		}

		return true;
	}


	/**
	 * Macht eine Zustands�nderung mit der angebenene Bedingung.
	 * 
	 * @param condition
	 *          Die Bedingung f�r die Zustand�nderung.
	 * @return Der Zustand, der durch diese Zustand�nderung erreicht worden ist.
	 *         Ist keine Zustand�nderung m�glich wird null ausgegeben und der
	 *         aktuelle Zustand bleibt unver�ndert.
	 * @remarks (1) null-�berg�nge bzw. Epislon-�berg�nge werden nicht
	 *          ber�cksichtigt, k�nnen aber explizit, wie normale �berg�nge durch
	 *          die Angabe von null als �bergangsbedinung durchgef�hrt werden. (2)
	 *          Gibt es mehr als einen passenden �bergang wird Standardm��ig der
	 *          erste passende �bergang ausgew�hlt.
	 */
	public State<TransitionConditionType, StatePayloadType> changeState(
			TransitionConditionType condition) {
		for ( Transition<TransitionConditionType, StatePayloadType> transition : getCurrentState()
				.getTransitions()) {
			if ( transition.getCondition().equals( condition)) {
				State<TransitionConditionType, StatePayloadType> state = transition
						.getState();
				_currentState = state;
				return state;
			}
		}

		return null;
	}


	/**
	 * Gibt an, ob eine Zustand�nderung mit der angegebenen Bedingung m�glich ist.
	 * 
	 * @param condition
	 *          Die Bedingung f�r die Zustand�nderung.
	 * @return true, wenn eine Zustand�nderung m�glich ist, sonst false.
	 * @remarks null-�berg�nge bzw. Epislon-�berg�nge werden nicht ber�cksichtigt,
	 *          k�nnen aber explizit, wie normale �berg�nge durch die Angabe von
	 *          null als �bergangsbedinung �berpr�ft werden.
	 */
	public boolean canChangeState( TransitionConditionType condition) {
		for ( Transition<TransitionConditionType, StatePayloadType> transition : getCurrentState()
				.getTransitions()) {
			if ( transition.getCondition().equals( condition))
				return true;
		}

		return false;
	}


	/**
	 * Setzt den aktuellen Zustand auf den Startzustand zur�ck.
	 */
	public void resetToInitialState() {
		_currentState = _initialState;
	}


	/**
	 * F�gt einen neuen �bergang mit angegebener Bedingung von einem
	 * Ausgangszustand in einen Zielzustand ein.
	 * 
	 * @param sourceState
	 *          Der Ausgangszustand, von dem ein neuer �bergang erstellt werden
	 *          soll.
	 * @param destinationState
	 *          Der Zielzustand, in dem der neue �bergang zeigen soll.
	 * @param condition
	 *          Die Bedingung f�r den Zustand�bergang (null f�r einen
	 *          Epsilon-�bergang).
	 * @throws NullStateException
	 *           Wenn null als Parameter f�r den Ausgangszustand oder Zielzustand
	 *           �bergeben wird.
	 * @throws StateNotReachableException
	 *           Wenn der Ausgangszustand nicht erreichbar oder Teil des endlichen
	 *           Automats ist.
	 * @throws TransitionAlreadyExistsException
	 *           Wenn der �bergang bereits vorhanden ist.
	 */
	public void addTransition(
			State<TransitionConditionType, StatePayloadType> sourceState,
			State<TransitionConditionType, StatePayloadType> destinationState,
			TransitionConditionType condition) throws NullStateException,
			StateNotReachableException, TransitionAlreadyExistsException {
		if ( sourceState == null || destinationState == null)
			throw new NullStateException();
		if ( !containsStateWithUUID( sourceState.getUUID()))
			throw new StateNotReachableException();

		if ( !containsStateWithUUID( destinationState.getUUID())) {
			_states.put( destinationState.getUUID(), destinationState);
		}
		sourceState.addState( condition, destinationState);
	}


	/**
	 * F�gt einen neuen �bergang mit angegebener Bedingung von aktuellen Zustand
	 * in einen Zielzustand ein.
	 * 
	 * @param destinationState
	 *          Der Zielzustand, in dem der neue �bergang zeigen soll.
	 * @param condition
	 *          Die Bedingung f�r den Zustand�bergang (null f�r einen
	 *          Epsilon-�bergang).
	 * @throws NullStateException
	 *           Wenn null als Parameter f�r den Zielzustand �bergeben wird.
	 * @throws TransitionAlreadyExistsException
	 *           Wenn der �bergang bereits vorhanden ist.
	 */
	public void addTransition(
			State<TransitionConditionType, StatePayloadType> destinationState,
			TransitionConditionType condition) throws NullStateException,
			TransitionAlreadyExistsException {
		if ( destinationState == null)
			throw new NullStateException();
		try {
			addTransition( _currentState, destinationState, condition);
		} catch ( StateNotReachableException e) {
			// Dieser Fall kann niemals eintreten!
			e.printStackTrace();
		}
	}


	/**
	 * Erstellt ein neues FiniteStateMachine Objekt. Dabei wird direkt ein
	 * Startzustand f�r diesen endlichen Automaten erstellt und als aktuellen
	 * Zustand gesetzt.
	 */
	public FiniteStateMachine() {
		State<TransitionConditionType, StatePayloadType> state = new State<TransitionConditionType, StatePayloadType>();
		state.setType( StateType.INITIAL);
		_initialState = state;
		_currentState = state;
		_states = new HashMap<UUID, State<TransitionConditionType, StatePayloadType>>();
		_states.put( state.getUUID(), state);
	}
}
