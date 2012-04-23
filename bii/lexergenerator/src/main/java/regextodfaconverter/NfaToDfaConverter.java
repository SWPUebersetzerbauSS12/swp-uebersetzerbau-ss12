package regextodfaconverter;

import regextodfaconverter.fsm.FiniteStateMachine;

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
		// TODO: convertToDfa implementieren.
		return finiteStateMachine;
	}

}
