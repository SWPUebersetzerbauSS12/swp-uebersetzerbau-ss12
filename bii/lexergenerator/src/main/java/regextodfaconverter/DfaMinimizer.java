package regextodfaconverter;

import regextodfaconverter.fsm.FiniteStateMachine;

/**
 * Stellt einen Konverter dar, der aus aus einem deterministischen endlichen
 * Automaten (deterministic finite automaton, kurz DFA) einen neuen minimalen
 * deterministischen endlichen Automaten erstellt.
 * 
 * @author ?
 * 
 * @param <TransitionConditionType>
 *            Der Typ der Bedingung für einen Zustandsübergang beim verwendeten
 *            endlichen Automaten.
 * @param <StatePayloadType>
 *            Der Typ des Inhalts der Zustände beim verwendeten endlichen
 *            Automaten.
 */
public class DfaMinimizer<TransitionConditionType extends Comparable<TransitionConditionType>, StatePayloadType> {

	/**
	 * Macht aus dem angegebenen (deterministischen) endlichen Automaten einen minimalen deterministischen endlichen Automaten.
	 * @param finiteStateMachine Der (deterministischen) endlichen Automaten der minimiert werden soll.
	 * @return Der minimierte deterministischen endlichen Automaten.
	 */
	public FiniteStateMachine<TransitionConditionType, StatePayloadType> convertToMimimumDfa(
			FiniteStateMachine<TransitionConditionType, StatePayloadType> finiteStateMachine) {
		if (!finiteStateMachine.isDeterministic())
		{
			//TODO: Exception werfen, da nur ausgehend von einem DFA ein minimaler DFA erstellt werden kann.
		}
		// TODO convertToMimimumDfa implementieren.
		return finiteStateMachine;
	}

}
