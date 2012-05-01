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
