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

import java.io.Serializable;

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
public class RegexToNfaConverter<StatePayloadType extends Serializable> {

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
		// Angegebenen Regex aif Basisoperationen reduzieren und klammern.
		try {
			regex = Regex.reduceAndBracketRegex(regex);
		} catch (RegexInvalidException e) {
			throw new ConvertExecption(
					"Der verwendete reguläre Ausdruck '"
							+ regex
							+ "' ist ungültig oder verwendet nicht unterstütze Operatoren!");
		}

		return convertRekursivToNFA(regex, payload);
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
	 * Erstellt aus dem angegebenen regulären Ausdruck rekursiv einen
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
	private FiniteStateMachine<Character, StatePayloadType> convertRekursivToNFA(
			String regex, StatePayloadType payload) throws ConvertExecption {
		FiniteStateMachine<Character, StatePayloadType> fsm = null;

		if (regex.length() == 0) {
			fsm = createSimpleNfa(null, payload);
		} else if (regex.length() == 1) {
			fsm = createSimpleNfa(regex.charAt(0), payload);
		} else if (regex.length() == 2 && regex.startsWith("\\")) {
			fsm = createSimpleNfa(regex.charAt(1), payload);
		} else if (regex.charAt(regex.length() - 1) == '*') {
			fsm = convertRekursivToNFA(regex.substring(1, regex.length() - 2),
					payload);
			fsm.closure();
		} else {
			// (...)|(...) oder (...)(...) oder (...)
			StringBuilder sb = new StringBuilder("");
			int opened = 1;
			for (int i = 1; i < regex.length(); i++) {
				char c = regex.charAt(i);
				if (c == '\\') {
					sb.append(c);
					if (i + 1 < regex.length()) {
						sb.append(regex.charAt(i + 1));
					}
					i++;
				} else if (c == '(') {
					sb.append(c);
					opened++;
				} else if (c == ')') {
					opened--;
					if (opened == 0) {
						break;
					} else {
						sb.append(c);
					}
				} else {
					sb.append(c);
				}
			}

			if (sb.toString().length() == regex.length() - 2) {
				// Fall (...)
				fsm = convertRekursivToNFA(sb.toString(), payload);
			} else {
				char c = regex.charAt(sb.toString().length() + 2);
				if (c == '|') {
					// Fall (...)|(...)
					fsm = convertRekursivToNFA(sb.toString(), payload);
					fsm.union(convertRekursivToNFA(
							regex.substring(sb.toString().length() + 4,
									regex.length() - 1), payload));
				} else if (c == '(') {
					// Fall (...)(...)
					fsm = convertRekursivToNFA(sb.toString(), payload);
					fsm.concat(convertRekursivToNFA(
							regex.substring(sb.toString().length() + 3,
									regex.length() - 1), payload));
				}
			}
		}

		NfaToDfaConverter<Character, StatePayloadType> converter = new NfaToDfaConverter<Character, StatePayloadType>();
		fsm = converter.convertToDfa(fsm);
		return fsm;
	}

	/**
	 * Erstellt eine einfachen nichtdeterministischen endlichen Automaten
	 * (Startzustand,Bedingung,Endzustand) mit der angegebenen Bedingung für den
	 * Übergang.
	 * 
	 * @param condition
	 *            Die Bedingung für den Übergang.
	 * @param payload
	 *            Der Inhalt des Endzustands.
	 * @return Eine einfacher nichtdeterministischen endlichen Automaten
	 *         (Startzustand,Bedingung,Endzustand) mit der angegebenen Bedingung
	 *         für den Übergang.
	 */
	private FiniteStateMachine<Character, StatePayloadType> createSimpleNfa(
			Character condition, StatePayloadType payload) {
		FiniteStateMachine<Character, StatePayloadType> nfa = new FiniteStateMachine<Character, StatePayloadType>();
		State<Character, StatePayloadType> state1;
		State<Character, StatePayloadType> state2;

		state1 = nfa.getCurrentState();
		state2 = new State<Character, StatePayloadType>(payload, true);

		try {
			nfa.addTransition(state1, state2, condition);
		} catch (Exception e) {
			// Dieser Fall kann nicht eintreten!
		}

		return nfa;
	}
}
