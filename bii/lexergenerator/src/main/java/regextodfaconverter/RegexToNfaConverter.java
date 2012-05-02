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

package regextodfaconverter;

import java.util.ArrayList;

import regextodfaconverter.fsm.FiniteStateMachine;
import regextodfaconverter.fsm.State;

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
	 * Erstellt aus dem angegebenen regulären Ausdruck einen
	 * nichtdeterministischen endlichen Automaten (nondeterministic finite
	 * automaton, kurz NFA).
	 * 
	 * @param Regex
	 *            Der reguläre Ausdruck, aus dem der NFA erstellt werden soll.
	 * @param payload
	 *            Der Inhalt, der in jedem Endzustand verknüpft werden soll.
	 * @return Der NFA, der durch den regulären Ausdruck abgebildet wird. Bei
	 *         einem leeren Regex wird null zurückgegeben.
	 * @remakrs Es werden nur die folgenden regulären Muster unterstützt: A|B,
	 *          AB, A*
	 * @throws ConvertExecption
	 *             Wenn ein Fehler beim Übersetzen des regulären Asudrucks in
	 *             einen NFA auftritt.
	 */
	public FiniteStateMachine<Character, StatePayloadType> convertToNFA(
			String regex, StatePayloadType payload) throws ConvertExecption {
		ArrayList<FiniteStateMachine<Character, StatePayloadType>> nfas = new ArrayList<FiniteStateMachine<Character, StatePayloadType>>();

		// Die nachfolgenden Character werden als Hilfs-Character verwendet und
		// dürfen daher nicht in dem regulären Ausdruck vorkommen:
		char cOpen = (char) 255; // ( -> (char)255
		char cClose = (char) 254; // ) -> (char)254
		char cGuard = (char) 253; // | -> (char)253
		char cStar = (char) 252; // * -> (char)252
		char cNFA = (char) 251; // NFA -> (char)251

		// Sicherstellen, dass in der Eingabe keins dieser Zeichen verwendet
		// wird:
		if (regex.indexOf(cOpen) != -1) {
			throw new ConvertExecption(
					"Der reguläre Ausdruck enthält nicht unterstützte Zeichen: '"
							+ cOpen + "'");
		}
		if (regex.indexOf(cClose) != -1) {
			throw new ConvertExecption(
					"Der reguläre Ausdruck enthält nicht unterstützte Zeichen: '"
							+ cClose + "'");
		}
		if (regex.indexOf(cGuard) != -1) {
			throw new ConvertExecption(
					"Der reguläre Ausdruck enthält nicht unterstützte Zeichen: '"
							+ cGuard + "'");
		}
		if (regex.indexOf(cStar) != -1) {
			throw new ConvertExecption(
					"Der reguläre Ausdruck enthält nicht unterstützte Zeichen: '"
							+ cStar + "'");
		}
		if (regex.indexOf(cNFA) != -1) {
			throw new ConvertExecption(
					"Der reguläre Ausdruck enthält nicht unterstützte Zeichen: '"
							+ cNFA + "'");
		}

		// Eingegebenen Regex minimieren
		try {
			regex = Regex.reduceRegexAndAddMissingParenthesis(regex);
		} catch (RegexInvalidException e) {
			throw new ConvertExecption(
					"Der verwendete reguläre Ausdruck '"
							+ regex
							+ "' ist ungültig oder verwendet nicht unterstütze Operatoren");
		}

		// Chars durch Hilfs-Character austauschen
		regex = regex.replace('(', cOpen);
		regex = regex.replace(')', cClose);
		regex = regex.replace('|', cGuard);
		regex = regex.replace('*', cStar);

		// Durch Escape-Char fälschlich ausgetauschte Chars wiederherstellen
		// (ohne Escape-Char)
		regex = regex.replace("\\" + cOpen, "(");
		regex = regex.replace("\\" + cClose, ")");
		regex = regex.replace("\\" + cGuard, "|");
		regex = regex.replace("\\" + cStar, "*");

		// Nach weiteren Escape-Char Sequenzen suchen
		char[] metaCharsWithoutEscapeChar = { '[', ']', '(', ')', '{', '}',
				'|', '?', '+', '-', '*', '^', '$', '.' };
		for (char c : metaCharsWithoutEscapeChar) {
			regex = regex.replace("\\" + c, "" + c);
		}
		regex = regex.replace("\\\\", "\\"); // Dieser Replace muss auf jeden
												// Fall der letzte sein!
		// Weitere Escape-Char Sequenzen darf es nicht geben, wenn die
		// Regex-Klasse richtig arbeitet

		// Klammerausdrücke ohne Inhalt "()" entfernen.
		while (regex.contains("" + cOpen + cClose)) {
			regex = regex.replace("" + cOpen + cClose, "");
		}

		// Regex-String ist nun hinreichend verarbeitet und bereit zur
		// Automatenerstellung.

		// Verarbeite den regex bis keine Klammern (bzw. geschlossene Klammern)
		// mehr vorhanden sind.
		while (regex.indexOf(cClose) != -1) {
			int indexClose = regex.indexOf(cClose);
			int indexOpen = regex.substring(0, indexClose + 1).lastIndexOf(
					cOpen);
			String subRegex = regex.substring(indexOpen, indexClose + 1);

			if (subRegex.length() < 3) {
				// Dieser Fall sollte im Prinzip nicht eintreten können.
				throw new ConvertExecption(
						"Unbekannter Ausnahmefehler. Fehlercode: w-l2");
			} else if (subRegex.length() == 3) {
				// Muster gefunden: (A) oder (NFA).
				if (subRegex.equals("" + cOpen + cNFA + cClose)) {
					// Muster gefunden: (NFA) --> NFA
				} else {
					// Muster gefunden: (A) --> new(NFA)

					// NFA erstellen
					FiniteStateMachine<Character, StatePayloadType> nfa = new FiniteStateMachine<Character, StatePayloadType>();
					State<Character, StatePayloadType> state1;
					State<Character, StatePayloadType> state2;

					state1 = nfa.getCurrentState();
					state2 = new State<Character, StatePayloadType>(payload,
							true);

					try {
						nfa.addTransition(state1, state2, subRegex.charAt(1));
					} catch (Exception e) {
						// Dieser Fall sollte im Prinzip nicht eintreten können.
						throw new ConvertExecption(
								"Unbekannter Ausnahmefehler. Fehlercode: w-l3");
					}
					// NFA erstellt

					// NFA ablegen
					nfas.add(nfa);
				}
				regex = replaceRangeInString(regex, indexOpen, indexClose + 1,
						"" + cNFA);
				continue;
			} else if (subRegex.length() == 4) {
				// Muster gefunden: (FSM*) oder (FSMFSM)
				if (subRegex.equals("" + cOpen + cNFA + cStar + cClose)) {
					// Muster gefunden: (FSM*) --> closure(NFA)
					int c = countCharFrequencyInString(
							regex.substring(0, indexClose - 3), cNFA);
					nfas.get(c).closure();
				} else if (subRegex.equals("" + cOpen + cNFA + cNFA + cClose)) {
					// Muster gefunden: (FSMFSM) --> concat(FSM,FSM)
					int c = countCharFrequencyInString(
							regex.substring(0, indexClose - 3), cNFA);
					nfas.get(c).concat(nfas.get(c + 1));
					nfas.remove(c + 1);
				} else {
					// Dieser Fall sollte im Prinzip nicht eintreten können.
					throw new ConvertExecption(
							"Unbekannter Ausnahmefehler. Fehlercode: w-l4");
				}
				regex = replaceRangeInString(regex, indexOpen, indexClose + 1,
						"" + cNFA);
				continue;
			} else if (subRegex.length() == 5) {
				// Muster gefunden: (FSM|FSM)
				if (subRegex.equals("" + cOpen + cNFA + cGuard + cNFA + cClose)) {
					// Muster gefunden: (FSM|FSM) --> union(FSM,FSM)
					int c = countCharFrequencyInString(
							regex.substring(0, indexClose - 3), cNFA);
					nfas.get(c).union(nfas.get(c + 1));
					nfas.remove(c + 1);
				} else {
					// Dieser Fall sollte im Prinzip nicht eintreten können.
					throw new ConvertExecption(
							"Unbekannter Ausnahmefehler. Fehlercode: w-l5");
				}
				regex = replaceRangeInString(regex, indexOpen, indexClose + 1,
						"" + cNFA);
				continue;
			} else if (subRegex.length() > 5) {
				// Dieser Fall sollte im Prinzip nicht eintreten können.
				throw new ConvertExecption(
						"Unbekannter Ausnahmefehler. Fehlercode: w-l6");
			}
		}

		if (nfas.size() == 0) {
			return null;
		} else if (nfas.size() == 1) {
			return nfas.get(0);
		} else {
			// Dieser Fall sollte im Prinzip nicht eintreten können.
			throw new ConvertExecption(
					"Unbekannter Ausnahmefehler. Fehlercode: r-s2");
		}
	}

	/**
	 * Erstellt aus dem angegebenen regulären Ausdruck einen
	 * nichtdeterministischen endlichen Automaten (nondeterministic finite
	 * automaton, kurz NFA).
	 * 
	 * @param Regex
	 *            Der reguläre Ausdruck, aus dem der NFA erstellt werden soll.
	 * @return Der NFA, der durch den regulären Ausdruck abgebildet wird. Bei
	 *         einem leeren Regex wird null zurückgegeben.
	 * @remakrs Es werden nur die folgenden regulären Muster unterstützt: A|B,
	 *          AB, A*
	 * @throws ConvertExecption
	 *             Wenn ein Fehler beim Übersetzen des regulären Asudrucks in
	 *             einen NFA auftritt.
	 */
	public FiniteStateMachine<Character, StatePayloadType> convertToNFA(
			String regex) throws ConvertExecption {
		return convertToNFA(regex, null);
	}

	/**
	 * Ersetzt in den angegebenen String den angegebenen Bereich mit dem
	 * angegebenen Inhalt.
	 * 
	 * @param inputString
	 *            Der Eingabe String, in dem der Bereich ersetzt werden soll.
	 * @param beginIndex
	 *            Der Start-Index des Bereichs im Eingabe-String, der ersetzt
	 *            werden soll.
	 * @param endIndex
	 *            Der End-Index des Bereichs im Eingabe-String, der ersetzt
	 *            werden soll.
	 * @param replaceString
	 *            Der Inhalt, mit dem der angegebene Berech ersetzt werden soll.
	 * @return
	 */
	private String replaceRangeInString(String inputString, int beginIndex,
			int endIndex, String replaceString) {
		return inputString.substring(0, beginIndex) + replaceString
				+ inputString.substring(endIndex);
	}

	/**
	 * Gibt die Häufigkeit des angegebenen Zeichens innerhalb des angegebenen
	 * Strings zurück.
	 * 
	 * @param inputString
	 *            Der String in dem die Häufigkeit gezählt werden soll.
	 * @param c
	 *            Das Zeichen, das gezählt werden soll.
	 * @return Die Häufigkeit des angegebenen Zeichens innerhalb des angegebenen
	 *         Strings.
	 */
	private int countCharFrequencyInString(String inputString, char c) {
		int count = 0;

		for (int i = 0; i < inputString.length(); i++) {
			if (inputString.charAt(i) == c) {
				count++;
			}
		}

		return count;
	}
}
