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
 *            Der Typ der Bedingung für einen Zustandsübergang.
 * @param <StatePayloadType>
 *            Der Typ des Inhalts der Zustände.
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
	 * Gibt den Zustand zurück, der die angegebene eindeutige UUID hat.
	 * 
	 * @return Die eindeutige ID des Zustands, der zurückgegeben werden soll.
	 *         Gibt es keinen Zustand, der die angegebene ID enthält wird null
	 *         zurückgegeben.
	 */
	public State<TransitionConditionType, StatePayloadType> getStateByUUID(
			UUID uuid) {
		if (_states.containsKey(uuid)) {
			return _states.get(uuid);
		} else {
			return null;
		}
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
		return _states.containsKey(uuid);
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
		for (State<TransitionConditionType, StatePayloadType> state : _states
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
			if (transition.getCondition().equals(condition)) {
				State<TransitionConditionType, StatePayloadType> state = transition
						.getState();
				_currentState = state;
				return state;
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
		_currentState = _initialState;
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
	 * @throws NullStateException
	 *             Wenn null als Parameter für den Ausgangszustand oder
	 *             Zielzustand übergeben wird.
	 * @throws StateNotReachableException
	 *             Wenn der Ausgangszustand nicht erreichbar oder Teil des
	 *             endlichen Automats ist.
	 * @throws TransitionAlreadyExistsException
	 *             Wenn der Übergang bereits vorhanden ist.
	 */
	public void addTransition(
			State<TransitionConditionType, StatePayloadType> sourceState,
			State<TransitionConditionType, StatePayloadType> destinationState,
			TransitionConditionType condition) throws NullStateException,
			StateNotReachableException, TransitionAlreadyExistsException {
		if (sourceState == null || destinationState == null)
			throw new NullStateException();
		if (!containsStateWithUUID(sourceState.getUUID()))
			throw new StateNotReachableException();

		if (!containsStateWithUUID(destinationState.getUUID())) {
			_states.put(destinationState.getUUID(), destinationState);
		}
		sourceState.addState(condition, destinationState);
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
	 * @throws NullStateException
	 *             Wenn null als Parameter für den Zielzustand übergeben wird.
	 * @throws TransitionAlreadyExistsException
	 *             Wenn der Übergang bereits vorhanden ist.
	 */
	public void addTransition(
			State<TransitionConditionType, StatePayloadType> destinationState,
			TransitionConditionType condition) throws NullStateException,
			TransitionAlreadyExistsException {
		if (destinationState == null)
			throw new NullStateException();
		try {
			addTransition(_currentState, destinationState, condition);
		} catch (StateNotReachableException e) {
			// Dieser Fall kann niemals eintreten!
			e.printStackTrace();
		}
	}

	/**
	 * Erstellt ein neues FiniteStateMachine Objekt. Dabei wird direkt ein
	 * Startzustand für diesen endlichen Automaten erstellt und als aktuellen
	 * Zustand gesetzt.
	 */
	public FiniteStateMachine() {
		State<TransitionConditionType, StatePayloadType> state = new State<TransitionConditionType, StatePayloadType>();
		state.setType(StateType.INITIAL);
		_initialState = state;
		_currentState = state;
		_states = new HashMap<UUID, State<TransitionConditionType, StatePayloadType>>();
		_states.put(state.getUUID(), state);
	}
}
