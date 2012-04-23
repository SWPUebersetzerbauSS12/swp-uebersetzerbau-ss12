package regextodfaconverter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import regextodfaconverter.fsm.FiniteStateMachine;
import regextodfaconverter.fsm.State;
import regextodfaconverter.fsm.Transition;

/**
 * Stellt einen Konverter dar, der aus einem nichtdeterministischen endlichen
 * Automaten (nondeterministic finite automaton, kurz NFA) einen neuen
 * deterministischen endlichen Automaten (deterministic finite automaton, kurz
 * DFA) erstellt.
 * 
 * @author Daniel Rotar
 * 
 * @param <TransitionConditionType>
 *            Der Typ der Bedingung für einen Zustandsübergang beim verwendeten
 *            endlichen Automaten.
 * @param <StatePayloadType>
 *            Der Typ des Inhalts der Zustände beim verwendeten endlichen
 *            Automaten.
 */
public class NfaToDfaConverter<TransitionConditionType extends Comparable<TransitionConditionType>, StatePayloadType> {

	/**
	 * Macht aus dem angegebenen endlichen Automaten einen deterministischen
	 * endlichen Automaten (deterministic finite automaton, kurz DFA).
	 * 
	 * @param finiteStateMachine
	 *            Der endliche Automat, aus dem ein DFA erstellt werden soll.
	 * @return Der DFA, der aus dem angegebenen endlichen Automaten erstellt
	 *         worden ist.
	 */
	public FiniteStateMachine<TransitionConditionType, StatePayloadType> convertToDfa(
			FiniteStateMachine<TransitionConditionType, StatePayloadType> finiteStateMachine) {
		FiniteStateMachine<TransitionConditionType, StatePayloadType> dfa = new FiniteStateMachine<TransitionConditionType, StatePayloadType>();

		HashMap<HashMap<UUID, State<TransitionConditionType, StatePayloadType>>, State<TransitionConditionType, StatePayloadType>> mergedStates = new HashMap<HashMap<UUID, State<TransitionConditionType, StatePayloadType>>, State<TransitionConditionType, StatePayloadType>>();

		Queue<DFATask> tasks = new LinkedList<DFATask>();
		HashMap<UUID, State<TransitionConditionType, StatePayloadType>> initialNFAStates = getAllStatesByCondition(
				finiteStateMachine.getInitialState(), null);
		initialNFAStates.put(finiteStateMachine.getInitialState().getUUID(),
				finiteStateMachine.getInitialState());
		tasks.add(new DFATask(dfa.getInitialState(), initialNFAStates));

		mergedStates.put(initialNFAStates, dfa.getInitialState());

		while (!tasks.isEmpty()) {
			DFATask task = tasks.poll();

			HashSet<TransitionConditionType> conditions = new HashSet<TransitionConditionType>();
			for (State<TransitionConditionType, StatePayloadType> state : task
					.getNFAStates().values()) {
				conditions.addAll(getAllConditions(state));
			}

			for (TransitionConditionType condition : conditions) {
				if (condition != null) {
					HashMap<UUID, State<TransitionConditionType, StatePayloadType>> reachableStates = new HashMap<UUID, State<TransitionConditionType, StatePayloadType>>();
					for (State<TransitionConditionType, StatePayloadType> state : task
							.getNFAStates().values()) {
						reachableStates.putAll(getAllStatesByCondition(state,
								condition));
					}

					if (!mergedStates.containsKey(reachableStates)) {
						State<TransitionConditionType, StatePayloadType> state = new State<TransitionConditionType, StatePayloadType>();

						for (State<TransitionConditionType, StatePayloadType> s : reachableStates.values())
						{
							if (s.isFiniteState())
							{
								state.SetTypeToFinite();
								state.setPayload(s.getPayload());
								break;
							}							
						}
						mergedStates.put(reachableStates, state);
						tasks.add(new DFATask(state, reachableStates));
					}
					try {
						dfa.addTransition(task.getDFAState(),
								mergedStates.get(reachableStates), condition);
					} catch (Exception e) {
						// Dieser Fall kann niemals eintreten!
						e.printStackTrace();
					}
				}
			}
		}

		return dfa;
	}

	/**
	 * Gibt ausgehend von einem Zustand alle erreichbaren Zustände, die durch
	 * die Bedingung und anschließend beliebig vieler Epsilon-Übergänge
	 * erreichbar sind zurück.
	 * 
	 * @param state
	 *            Der Ausgangszustand.
	 * @param condition
	 *            Die Bedingung für den Zustandübergang.
	 * @return Alle Zustände die durch die Bedingung und anschließend beliebig
	 *         vieler Epsilonübergänge erreichbar sind.
	 */
	private HashMap<UUID, State<TransitionConditionType, StatePayloadType>> getAllStatesByCondition(
			State<TransitionConditionType, StatePayloadType> state,
			TransitionConditionType condition) {
		int depth = 0;
		HashMap<UUID, State<TransitionConditionType, StatePayloadType>> visited = new HashMap<UUID, State<TransitionConditionType, StatePayloadType>>();

		Queue<State<TransitionConditionType, StatePayloadType>> tasks = new LinkedList<State<TransitionConditionType, StatePayloadType>>();
		tasks.add(state);

		while (!tasks.isEmpty()) {
			State<TransitionConditionType, StatePayloadType> task = tasks
					.poll();

			for (Transition<TransitionConditionType, StatePayloadType> transition : task
					.getTransitions()) {
				if (transition.getCondition() == null)
				{
					if (depth > 0 || condition == null)
					{
						visited.put(transition.getState().getUUID(),transition.getState());
					}
				}
				else
				{
					if (transition.getCondition().equals(condition))
					{
						visited.put(transition.getState().getUUID(),transition.getState());
					}
				}
			}

			depth++;
		}

		return visited;
	}

	/**
	 * Gibt für den angegebenen Zustand alle möglichen Bedingungen für einen
	 * Zustandwechsel zurück (Ein null-Übergang bzw. Epsilon-Übergang wird als
	 * eine "normale" Bedingung behandelt).
	 * 
	 * @param state
	 *            Der Ausgangszustand.
	 * @return Alle Bedingungen mit denen ein neuer Zustand erreicht werden
	 *         kann.
	 */
	private HashSet<TransitionConditionType> getAllConditions(
			State<TransitionConditionType, StatePayloadType> state) {
		HashSet<TransitionConditionType> conditions = new HashSet<TransitionConditionType>();

		for (Transition<TransitionConditionType, StatePayloadType> transition : state
				.getTransitions()) {
			conditions.add(transition.getCondition());
		}
		return conditions;
	}

	/**
	 * Stellt ein Aufgabenobjekt beim Konvertieren eines NFA zu einem DFA dar.
	 * 
	 * @author Daniel Rotar
	 * 
	 */
	private class DFATask {
		/**
		 * Der Zustand im DFA.
		 */
		private State<TransitionConditionType, StatePayloadType> _dfaState;
		/**
		 * Die Zustände in dem NFA.
		 */
		private HashMap<UUID, State<TransitionConditionType, StatePayloadType>> _nfaStates;

		/**
		 * Gibt den Zustand im DFA zurück.
		 * 
		 * @return Der Zustand im DFA.
		 */
		public State<TransitionConditionType, StatePayloadType> getDFAState() {
			return _dfaState;
		}

		/**
		 * Setzt den Zustand im DFA fest.
		 * 
		 * @param dfaState
		 *            Der Zustand im DFA.
		 */
		private void setDFAState(
				State<TransitionConditionType, StatePayloadType> dfaState) {
			_dfaState = dfaState;
		}

		/**
		 * Gibt die Zustände in dem NFA zurück.
		 * 
		 * @return Die Zustände in dem NFA.
		 */
		public HashMap<UUID, State<TransitionConditionType, StatePayloadType>> getNFAStates() {
			return _nfaStates;
		}

		/**
		 * Setzt die Zustände in dem NFA fest.
		 * 
		 * @param nfaStates
		 *            Die Zustände in dem NFA.
		 */
		private void setNFAStates(
				HashMap<UUID, State<TransitionConditionType, StatePayloadType>> nfaStates) {
			_nfaStates = nfaStates;
		}

		public DFATask(
				State<TransitionConditionType, StatePayloadType> dfaState,
				HashMap<UUID, State<TransitionConditionType, StatePayloadType>> nfaStates) {
			setDFAState(dfaState);
			setNFAStates(nfaStates);
		}
	}
}
