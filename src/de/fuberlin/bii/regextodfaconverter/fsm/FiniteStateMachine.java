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

package de.fuberlin.bii.regextodfaconverter.fsm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import de.fuberlin.bii.regextodfaconverter.fsm.excpetions.NullStateException;
import de.fuberlin.bii.regextodfaconverter.fsm.excpetions.StateNotReachableException;

/**
 * Stellt einen endlichen Automaten (finite state machine, kurz FSM) dar.
 * 
 * @author Daniel Rotar
 * 
 * @param <TransitionConditionType>
 *            Der Typ der Bedingung für einen Zustandsübergang.
 * @param <StatePayloadType>
 *            Der Typ des Inhalts der Zustände.
 */
public class FiniteStateMachine<TransitionConditionType extends Serializable, StatePayloadType extends Serializable>
		implements Serializable {

	/**
	 * UID für die Serialisierung/Abspeicherung als *.dfa
	 */
	private static final long serialVersionUID = 7451317869119939422L;

	/**
	 * Der Startzustand dieses endlichen Automatens.
	 */
	private State<TransitionConditionType, StatePayloadType> _initialState;
	/**
	 * Der aktuelle Zustand, in dem sich dieser endlichen Automatens befindet.
	 */
	private State<TransitionConditionType, StatePayloadType> _currentState;
	/**
	 * Die HashMap die alle Zustände dieses endlichen Automaten, geordnet nach
	 * ihren eindetige ID enthält.
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
	 * Setzt den Startzustand dieses endlichen Automatens.
	 * 
	 * @param state
	 *            Der neue Startzustand dieses endlichen Automatens.
	 * @throws NullStateException
	 *             Wenn null als Parameter für den neuen Zustand übergeben wird.
	 * @throws StateNotReachableException
	 *             Wenn der Zustand nicht erreichbar oder Teil des endlichen
	 *             Automats ist.
	 */
	protected void setInitialState(
			State<TransitionConditionType, StatePayloadType> initialState)
			throws NullStateException, StateNotReachableException {
		if (initialState == null)
			throw new NullStateException();
		if (!containsStateWithUUID(initialState.getUUID()))
			throw new StateNotReachableException();
		_initialState = initialState;
	}

	/**
	 * Gibt den aktuelle Zustand, in dem sich dieser endlichen Automatens
	 * befindet zurück.
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
	 *            Der neue Zustand, in dem sich dieser endlichen Automatens
	 *            befinden soll.
	 * @throws NullStateException
	 *             Wenn null als Parameter für den neuen Zustand übergeben wird.
	 * @throws StateNotReachableException
	 *             Wenn der Zustand nicht erreichbar oder Teil des endlichen
	 *             Automats ist.
	 */
	public void setCurrentState(
			State<TransitionConditionType, StatePayloadType> state)
			throws NullStateException, StateNotReachableException {
		if (state == null)
			throw new NullStateException();
		if (!containsStateWithUUID(state.getUUID()))
			throw new StateNotReachableException();
		_currentState = state;
	}

	/**
	 * Gibt die HashMap die alle Zustände dieses endlichen Automaten, geordnet
	 * nach ihren eindetige ID enthält zurück..
	 * 
	 * @return Die HashMap die alle Zustände dieses endlichen Automaten,
	 *         geordnet nach ihren eindetige ID enthält.
	 */
	public HashMap<UUID, State<TransitionConditionType, StatePayloadType>> getStates() {
		return _states;
	}

	/**
	 * Setzt die HashMap die alle Zustände dieses endlichen Automaten, geordnet
	 * nach ihren eindetige ID enthält fest.
	 * 
	 * @param states
	 *            Die HashMap die alle Zustände dieses endlichen Automaten,
	 *            geordnet nach ihren eindetige ID enthält.
	 */
	protected void setStates(
			HashMap<UUID, State<TransitionConditionType, StatePayloadType>> states) {
		_states = states;
	}

	/**
	 * Gibt den Zustand zurück, der die angegebene eindeutige UUID hat.
	 * 
	 * @return Die eindeutige ID des Zustands, der zurückgegeben werden soll.
	 *         Gibt es keinen Zustand, der die angegebene ID enthält wird null
	 *         zurückgegeben.
	 */
	public State<TransitionConditionType, StatePayloadType> getStateByUUID(
			UUID uuid) {
		return getStates().get(uuid);
	}

	/**
	 * Gibt an ob der endliche Automat einen Zustand mit der angegebenen
	 * eindeutigen UUID beinhaltet.
	 * 
	 * @param uuid
	 *            Die UUID nach der in diesem endliche Automaten gesucht werden
	 *            soll.
	 * @return true, wenn die UUID in dem endliche Automat vorhanden ist, sonst
	 *         false.
	 */
	public boolean containsStateWithUUID(UUID uuid) {
		return getStates().containsKey(uuid);
	}

	/**
	 * Gibt an, ob es sich bei diesem endlichen Automaten um einen
	 * deterministisch endlichen Automaten (deterministic finite automaton, kurz
	 * DFA) handelt.
	 * 
	 * @return true, wenn es sich um einen deterministisch endlichen Automaten
	 *         (deterministic finite automaton, kurz DFA) handelt, sonst false.
	 */
	public boolean isDeterministic() {
		for (State<TransitionConditionType, StatePayloadType> state : getStates()
				.values()) {
			HashSet<TransitionConditionType> conditions = new HashSet<TransitionConditionType>();
			for (Transition<TransitionConditionType, StatePayloadType> transition : state
					.getTransitions()) {
				if (transition.getCondition() == null)
					return false; // Epsilon-Übergang vorhanden
				if (!conditions.add(transition.getCondition()))
					return false; // Übergangsbedingung mehrfach vorhanden.
			}
		}

		return true;
	}

	/**
	 * Macht eine Zustandsänderung mit der angebenene Bedingung.
	 * 
	 * @param condition
	 *            Die Bedingung für die Zustandsänderung.
	 * @return Der Zustand, der durch diese Zustandsänderung erreicht worden
	 *         ist. Ist keine Zustandsänderung möglich wird null ausgegeben und
	 *         der aktuelle Zustand bleibt unverändert.
	 * @remarks (1) null-Übergänge bzw. Epislon-Übergänge werden nicht
	 *          berücksichtigt, können aber explizit, wie normale Übergänge
	 *          durch die Angabe von null als Übergangsbedinung durchgeführt
	 *          werden. (2) Gibt es mehr als einen passenden Übergang wird
	 *          standardmäßig der erste passende Übergang ausgewählt.
	 */
	public State<TransitionConditionType, StatePayloadType> changeState(
			TransitionConditionType condition) {
		for (Transition<TransitionConditionType, StatePayloadType> transition : getCurrentState()
				.getTransitions()) {
			if (transition.getCondition() != null) {
				if (transition.getCondition().equals(condition)) {
					State<TransitionConditionType, StatePayloadType> state = transition
							.getState();
					try {
						setCurrentState(state);
					} catch (Exception e) {
						// Dieser Fall kann niemals eintreten!
						e.printStackTrace();
					}
					return state;
				}
			} else {
				if (condition == null) {
					State<TransitionConditionType, StatePayloadType> state = transition
							.getState();
					try {
						setCurrentState(state);
					} catch (Exception e) {
						// Dieser Fall kann niemals eintreten!
						e.printStackTrace();
					}
					return state;
				}
			}

		}

		return null;
	}

	/**
	 * Gibt an, ob eine Zustandsänderung mit der angegebenen Bedingung möglich
	 * ist.
	 * 
	 * @param condition
	 *            Die Bedingung für die Zustandsänderung.
	 * @return true, wenn eine Zustandsänderung möglich ist, sonst false.
	 * @remarks null-Übergänge bzw. Epislon-Übergänge werden nicht
	 *          berücksichtigt, können aber explizit, wie normale Übergänge
	 *          durch die Angabe von null als Übergangsbedinung überprüft
	 *          werden.
	 */
	public boolean canChangeState(TransitionConditionType condition) {
		for (Transition<TransitionConditionType, StatePayloadType> transition : getCurrentState()
				.getTransitions()) {
			if (transition.getCondition().equals(condition))
				return true;
		}

		return false;
	}

	/**
	 * Setzt den aktuellen Zustand auf den Startzustand zurück.
	 */
	public void resetToInitialState() {
		try {
			setCurrentState(getInitialState());
		} catch (Exception e) {
			// Dieser Fall kann niemals eintreten!
			e.printStackTrace();
		}
	}

	/**
	 * Fügt einen neuen Übergang mit angegebener Bedingung von einem
	 * Ausgangszustand in einen Zielzustand ein.
	 * 
	 * @param sourceState
	 *            Der Ausgangszustand, von dem ein neuer Übergang erstellt
	 *            werden soll.
	 * @param destinationState
	 *            Der Zielzustand, in dem der neue Übergang zeigen soll.
	 * @param condition
	 *            Die Bedingung für den Zustandsübergang (null für einen
	 *            Epsilon-Übergang).
	 * @return true, wenn der Übergang (bzw. ein äquivalenter Übergang) noch nicht Vorhanden war, sonst false.
	 * @throws NullStateException
	 *             Wenn null als Parameter für den Ausgangszustand oder
	 *             Zielzustand übergeben wird.
	 * @throws StateNotReachableException
	 *             Wenn der Ausgangszustand nicht erreichbar oder Teil des
	 *             endlichen Automats ist.
	 */
	public boolean addTransition(
			State<TransitionConditionType, StatePayloadType> sourceState,
			State<TransitionConditionType, StatePayloadType> destinationState,
			TransitionConditionType condition) throws NullStateException,
			StateNotReachableException {
		if (sourceState == null || destinationState == null)
			throw new NullStateException();
		if (!containsStateWithUUID(sourceState.getUUID()))
			throw new StateNotReachableException();
		if (!containsStateWithUUID(destinationState.getUUID())) {
			getStates().put(destinationState.getUUID(), destinationState);
		}

		return sourceState.addState(condition, destinationState);
	}

	/**
	 * Fügt einen neuen Übergang mit angegebener Bedingung von aktuellen Zustand
	 * in einen Zielzustand ein.
	 * 
	 * @param destinationState
	 *            Der Zielzustand, in dem der neue Übergang zeigen soll.
	 * @param condition
	 *            Die Bedingung für den Zustandsübergang (null für einen
	 *            Epsilon-Übergang).
	 * @return true, wenn der Übergang (bzw. ein äquivalenter Übergang) noch nicht Vorhanden war, sonst false.
	 * @throws NullStateException
	 *             Wenn null als Parameter für den Zielzustand übergeben wird.
	 */
	public boolean addTransition(
			State<TransitionConditionType, StatePayloadType> destinationState,
			TransitionConditionType condition) throws NullStateException {
		if (destinationState == null)
			throw new NullStateException();
		try {
			return addTransition(getCurrentState(), destinationState, condition);
		} catch (StateNotReachableException e) {
			// Dieser Fall kann niemals eintreten!
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Vereinigt diesen endlichen Automaten mit dem angegebenen endlichen
	 * Automaten.
	 * 
	 * @param fsm
	 *            Der endliche Automat, mit dem dieser endliche Automat verenigt
	 *            werden soll.
	 * @remarks Durch diese Methode wird der aktuelle Zustand auf den
	 *          Startzustand zurückgesetzt.
	 */
	public void union(
			FiniteStateMachine<TransitionConditionType, StatePayloadType> fsm) {

		State<TransitionConditionType, StatePayloadType> state = new State<TransitionConditionType, StatePayloadType>();

		getStates().put(state.getUUID(), state);
		try {
			addTransition(state, getInitialState(), null);
		} catch (Exception e) {
			// Dieser Fall kann niemals eintreten!
			e.printStackTrace();
		}
		getInitialState().setInitial(false);

		try {
			setInitialState(state);
		} catch (Exception e) {
			// Dieser Fall kann niemals eintreten!
			e.printStackTrace();
		}
		state.setTypeToInitial();

		getStates().putAll(fsm.getStates());

		try {
			addTransition(state, fsm.getInitialState(), null);
		} catch (Exception e) {
			// Dieser Fall kann niemals eintreten!
			e.printStackTrace();
		}
		fsm.getInitialState().setInitial(false);

		resetToInitialState();
	}

	/**
	 * Verbindet diesen endlichen Automaten mit dem angegebenen endlichen
	 * Automaten.
	 * 
	 * @param fsm
	 *            Der endliche Automat, mit dem dieser endliche Automat
	 *            verbunden werden soll.
	 * @remarks Durch diese Methode wird der aktuelle Zustand auf den
	 *          Startzustand zurückgesetzt.
	 */
	public void concat(
			FiniteStateMachine<TransitionConditionType, StatePayloadType> fsm) {

		HashMap<UUID, State<TransitionConditionType, StatePayloadType>> states = new HashMap<UUID, State<TransitionConditionType, StatePayloadType>>();
		states.putAll(getStates());

		for (State<TransitionConditionType, StatePayloadType> state : states
				.values()) {
			if (state.isFiniteState()) {
				// state.setPayload(null);
				state.setTypeToDefault();
				try {
					addTransition(state, fsm.getInitialState(), null);
				} catch (Exception e) {
					// Dieser Fall kann niemals eintreten!
					e.printStackTrace();
				}
			}
		}

		getStates().putAll(fsm.getStates());
		fsm.getInitialState().setInitial(false);

		resetToInitialState();
	}

	/**
	 * Fügt diesem eindlichen Automaten eine Wiederholung hinzu.
	 * 
	 * @remarks Durch diese Methode wird der aktuelle Zustand auf den
	 *          Startzustand zurückgesetzt.
	 */
	public void closure() {
		HashMap<UUID, State<TransitionConditionType, StatePayloadType>> states = new HashMap<UUID, State<TransitionConditionType, StatePayloadType>>();
		states.putAll(getStates());

		State<TransitionConditionType, StatePayloadType> initState = new State<TransitionConditionType, StatePayloadType>();
		State<TransitionConditionType, StatePayloadType> finiteState = new State<TransitionConditionType, StatePayloadType>();
		State<TransitionConditionType, StatePayloadType> old_initState = getInitialState();

		initState.setTypeToInitial();
		finiteState.setTypeToFinite();

		getStates().put(initState.getUUID(), initState);
		getStates().put(finiteState.getUUID(), finiteState);

		try {
			addTransition(initState, finiteState, null);
		} catch (Exception e) {
			// Dieser Fall kann niemals eintreten!
			e.printStackTrace();
		}

		try {
			addTransition(initState, getInitialState(), null);
		} catch (Exception e) {
			// Dieser Fall kann niemals eintreten!
			e.printStackTrace();
		}
		getInitialState().setInitial(false);

		try {
			setInitialState(initState);
		} catch (Exception e) {
			// Dieser Fall kann niemals eintreten!
			e.printStackTrace();
		}

		for (State<TransitionConditionType, StatePayloadType> state : states
				.values()) {
			if (state.isFiniteState()) {
				// state.setPayload(null);
				finiteState.setPayload(state.getPayload());
				state.setTypeToDefault();
				try {
					addTransition(state, finiteState, null);
				} catch (Exception e) {
					// Dieser Fall kann niemals eintreten!
					e.printStackTrace();
				}

				try {
					addTransition(state, old_initState, null);
				} catch (Exception e) {
					// Dieser Fall kann niemals eintreten!
					e.printStackTrace();
				}
			}
		}

		resetToInitialState();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (State<TransitionConditionType, StatePayloadType> state : getStates()
				.values()) {
			if (state.isInitialState()) {
				sb.append(">");
			}
			sb.append(state.getUUID());
			if (state.isFiniteState()) {
				sb.append(">");
			}
			sb.append("\n");
			if (state.getTransitions().size() == 0) {
				sb.append("\t No outgoing transitions.\n");
			} else {
				for (Transition<TransitionConditionType, StatePayloadType> tran : state
						.getTransitions()) {
					sb.append("\t" + tran.getCondition() + " -> "
							+ tran.getState().getUUID() + "\n");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Erstellt ein neues FiniteStateMachine Objekt. Dabei wird direkt ein
	 * Startzustand für diesen endlichen Automaten erstellt und als aktuellen
	 * Zustand gesetzt.
	 */
	public FiniteStateMachine() {
		State<TransitionConditionType, StatePayloadType> state = new State<TransitionConditionType, StatePayloadType>();
		state.setTypeToInitial();

		setStates(new HashMap<UUID, State<TransitionConditionType, StatePayloadType>>());
		getStates().put(state.getUUID(), state);

		try {
			setInitialState(state);
			setCurrentState(state);
		} catch (Exception e) {
			// Dieser Fall kann niemals eintreten!
			e.printStackTrace();
		}
	}
}
