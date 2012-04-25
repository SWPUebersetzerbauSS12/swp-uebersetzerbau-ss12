package regextodfaconverter;

import regextodfaconverter.fsm.FiniteStateMachine;

/**
 * Stellt einen Konverter dar, der aus einem regulären Ausdruck einen
 * nichtdeterministischen endlichen Automaten (nondeterministic finite
 * automaton, kurz NFA) erstellt.
 * 
 * @author Daniel Rotar
 * 
 * @param <StatePayloadType>
 *            Der Typ des Inhalts der Zustände beim verwendeten endlichen
 *            Automaten.
 */
public class RegexToNfaConverter<StatePayloadType> {
	
	/**
	 * Erstellt aus dem angegebenen regulären Ausdruck einen nichtdeterministischen endlichen Automaten (nondeterministic finite automaton, kurz NFA).
	 * @param Regex Der reguläre Ausdruck, aus dem der NFA erstellt werden soll.
	 * @return Der NFA, der durch den regulären Ausdruck abgebildet wird.
	 * @remakrs Es werden nur die folgenden regulären Muster unterstützt: A|B, AB, A*
	 */
	public FiniteStateMachine<Character, StatePayloadType> convertToNFA(String Regex)
	{
		FiniteStateMachine<Character, StatePayloadType> nfa = new FiniteStateMachine<Character, StatePayloadType>();
		
		//TODO: convertToNFA implementieren.
		
		return nfa;
	}
}
