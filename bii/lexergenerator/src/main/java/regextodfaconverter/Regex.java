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

/**
 * Stellt grundlegende Funktionen zum Arbeiten mit regulären Ausdrücken bereit.
 * 
 * @author Daniel Rotar
 * 
 */
public class Regex {

	/**
	 * Die Zeichen, die reserviert für Operatoren sind (Diese Zeichen sind daher
	 * nicht Teil des alphabets).
	 */
	private static char[] RESERVED_CHARACTERS = { '[', ']', '(', ')', '{', '}',
			'|', '?', '+', '-', '*', '^', '$', '\\', '.' };

	// ASCII control characters: character code 0-31
	// ASCII printable characters: character code 32-127
	// The extended ASCII codes: character code 128-255
	/**
	 * Das erste Zeichen aus dem ASCII Zeichensatz, das im Alphabet enthalten
	 * sein soll.
	 */
	private static int FIRST_ASCII_CHAR = 33;
	/**
	 * Das letzte Zeichen aus dem ASCII Zeichensatz, das im Alphabet enthalten
	 * sein soll.
	 */
	private static int LAST_ASCII_CHAR = 126;

	/**
	 * Reduziert den angebenen regulären Ausdruck auf die Grundoperationen (AB,
	 * A|B, A*) und klammert den Ausdruck korrekt und vollständig.
	 * 
	 * @param regex
	 *            Der zu reduzierende und zu klammernde reguläre Ausdruck.
	 * @return Ein äquivalenter reduzierter und geklammerte regulärer Ausdruck.
	 * @throws RegexInvalidException
	 *             Wenn der angegebene regulärer Ausdruck ungültig ist oder
	 *             nicht unterstützt wird.
	 */
	public static String reduceAndAddParenthesis(String regex)
			throws RegexInvalidException {
		// Auf Grundoperationen reduzieren.
		regex = replaceDots(regex);
		regex = replaceBrackets(regex);
		
		// Klammerung einfügen.
		regex = addMissingParenthesis(regex);

		return regex;
	}

	/**
	 * Ersetzt jeden Punkt (".") durch eine Veroderung eines jeden gültige
	 * Zeichen aus dem Alpabeth, dass nicht reserviert ist
	 * ("(...|...|...|...)").
	 * 
	 * @param regex
	 *            Der zu reduzierende reguläre Ausdruck.
	 * @return Der reduzierte reguläre Ausdruck.
	 */
	private static String replaceDots(String regex) {
		StringBuilder sb = new StringBuilder("(");

		for (int i = FIRST_ASCII_CHAR; i <= LAST_ASCII_CHAR; i++) {
			char c = (char) i;

			if (!isReservedCharacter(c)) {
				sb.append("|" + c);
			}
		}
		sb.append(")");
		sb.delete(1, 2); // erstes "|" entfernen.

		String replaceWith = sb.toString();

		return regex.replace(".", replaceWith);
	}

	/**
	 * Ersetzt jedes vorkommen von "[...]" mit einem entsprechenden reduzierten
	 * regulären Ausdruck: Fall1: [first] wird ersetz durch (f|i|r|s|t). Fall2:
	 * [a-d] wird ersetzt durch (a|b|c|d). Fall3: [a-dA-D] wird ersetzt durch
	 * (a|b|c|d|A|B|C|D). Fall4: [^a] wird ersetzt durch eine (...|...|...|...)
	 * (jedes Zeichen aus dem Alphabet außer dem angegebenen Zeichen oder einem
	 * der reservierten Zeichen. Fall5: [-a-c], [a-c-] wird ersetzt durch
	 * (-|a|b|c) bzw. (a|b|c|-).
	 * 
	 * @param regex
	 * @return
	 * @throws RegexInvalidException
	 */
	private static String replaceBrackets(String regex)
			throws RegexInvalidException {

		return regex;
	}

	private static String addMissingParenthesis(String regex) {
		// TODO: addMissingParenthesis implementieren.
		return regex;
	}

	public static boolean isValid(String regex) {
		// TODO: isValid implementieren.
		return false;
	}

	public static boolean containsOnlyBasicOperations() {
		// TODO: containsOnlyBasicOperations implementieren.
		return false;
	}

	/**
	 * Gibt an ob es sich bei dem angegebenen Zeichen um ein reserviertes
	 * Zeichen handelt.
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn es sich um ein reserviertes Zeichen handelt, sonst
	 *         false.
	 */
	private static boolean isReservedCharacter(char c) {
		boolean reserved = false;

		for (char rc : RESERVED_CHARACTERS) {
			if (rc == c) {
				reserved = true;
				break;
			}
		}

		return reserved;
	}
}
