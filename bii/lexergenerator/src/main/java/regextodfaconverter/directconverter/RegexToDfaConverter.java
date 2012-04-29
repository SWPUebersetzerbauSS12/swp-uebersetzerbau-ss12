package regextodfaconverter.directconverter;

import regextodfaconverter.fsm.FiniteStateMachine;

/**
 * Stellt Funktionalitäten bereit, um einen vereinfachten regulären Ausdruck in eine DFA umzuwandeln. 
 * 
 * @author Johannes Dahlke
 * 
 * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/folien/Haenelt_FSA_RegExFSA.pdf">Fraunhofer Institut: Überführung regulärer Ausdrücke in endliche Automaten</a>
 * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/folien/Haenelt_RegEx-FSA-GMY.pdf">Fraunhofer Institut: Der Algorithmus von Glushkov und McNaughton/Yamada</a>
 * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/folien/FSA-RegA-6.pdf">Endliche Automaten: Reguläre Mengen, Reguläre Ausdrücke, reguläre Sprachen und endliche Automaten</a>
 * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/Skripten/FSA-Skript/Haenelt_EA_RegEx2EA.pdf">Überführung regulärer Ausdrücke in endliche Automaten</a>
 */
public class RegexToDfaConverter {
	

	/**
	 * Wandelt einen vereinfachten regulären Ausdruck in einen DFA um.
	 * 
	 * @param Regex der reguläre Ausdruck in vereinfachter Form.
	 * @param <StatePayloadType> der Inhalt, welcher Zuständen zugeordnet sein kann.
	 * @return ein DFA
	 * 
	 */	
	public static <StatePayloadType> FiniteStateMachine<Character, StatePayloadType> convert(String regex) {
		
		SyntaxTree syntaxTree = convertRegexToSyntaxTree( regex);
		FiniteStateMachine<Character, StatePayloadType> dfa = convertSyntaxTreeToDfa( syntaxTree);
		return dfa;
	}
	
  /**
   * 
   * @param Regex
   * @return
   */
	private static SyntaxTree convertRegexToSyntaxTree( String Regex) {
		return null;
	}
	
	private static <StatePayloadType> FiniteStateMachine<Character, StatePayloadType> convertSyntaxTreeToDfa( SyntaxTree syntaxTree) {
		//FiniteStateMachine<Character, StatePayloadType> dfa = new FiniteStateMachine<Character, StatePayloadType>();
		return null;
	}
		
	
	
}
