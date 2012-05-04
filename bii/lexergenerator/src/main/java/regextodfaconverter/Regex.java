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

/**
 * Stellt grundlegende Funktionen zum Arbeiten mit regulären Ausdrücken bereit.
 * 
 * @author Daniel Rotar
 * 
 */
public class Regex {

	/**
	 * Die Zeichen für Grundoperationen.
	 */
	private static char[] BASIC_META_CHARS = { '(', ')', '|', '*' };
	/**
	 * Die Zeichen für erweiterten Operationen.
	 */
	private static char[] EXTENDED_META_CHARS = { '[', ']', '{', '}', '?', '+',
			'-', '^', '$', '.' };
	/**
	 * Das Escape-Zeichen.
	 */
	private static char ESCAPE_META_CHAR = '\\';

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
	 * Reduziert den angebenen regulären Ausdruck auf die Grundoperationen und
	 * klammert den Ausdruck korrekt und vollständig.
	 * 
	 * @param regex
	 *            Der zu reduzierende und zu klammernde reguläre Ausdruck.
	 * @return Ein äquivalenter reduzierter und geklammerte regulärer Ausdruck.
	 * @throws RegexInvalidException
	 *             Wenn der angegebene regulärer Ausdruck ungültig ist oder
	 *             nicht unterstützt wird.
	 */
	public static String reduceRegexAndAddMissingParenthesis(String regex)
			throws RegexInvalidException {
		String output = reduceRegex(regex);
		System.out.println(output);
		return addMissingParenthesis(output);
		// TODO: wieder rückgängig machen

		// return addMissingParenthesis(reduceRegex(regex));
	}

	/**
	 * Reduziert den angebenen regulären Ausdruck auf die Grundoperationen.
	 * 
	 * @param regex
	 *            Der zu reduzierende und reguläre Ausdruck.
	 * @return Ein äquivalenter reduzierterregulärer Ausdruck.
	 * @throws RegexInvalidException
	 *             Wenn der angegebene regulärer Ausdruck ungültig ist oder
	 *             nicht unterstützt wird.
	 */
	protected static String reduceRegex(String regex)
			throws RegexInvalidException {
		// Regex auf gültige Zeichen überprüfen.
		for (int i = 0; i < regex.length(); i++) {
			if (!isValidChar(regex.charAt(i))) {
				throw new RegexInvalidException(
						"Der angegebene reguläre Ausdruck darf enthält ungültige Zeichen: '"
								+ regex.charAt(i) + "'!");
			}
		}

		String output = regex;
		output = replaceEscapeGroups(output);
		output = replaceDots(output);
		// TODO: Weitere erweiterte Operationen unterstützen...

		return output;
	}

	/**
	 * Klammert den angebenen regulären Ausdruck korrekt und vollständig. Der
	 * eingegebene reguläre Ausdruck darf dabei nur die Grundoperationen
	 * enthalten.
	 * 
	 * @param regex
	 *            Der zu klammernde reguläre Ausdruck (darf nur aus
	 *            Grundoperationen enthalten).
	 * @return Ein äquivalenter geklammerte regulärer Ausdruck.
	 * @throws RegexInvalidException
	 *             Wenn der angegebene regulärer Ausdruck ungültig ist oder
	 *             nicht unterstützt wird.
	 */
	protected static String addMissingParenthesis(String regex)
			throws RegexInvalidException {
		if (regex.length() == 0) {
			return "";
		}
		// Vorbereitungen treffen:

		// Sicherstellen, das der angegebene reguläre Ausdruck nur noch
		// Grundoperationen enthält.
		if (!containsOnlyBasicOperations(regex)) {
			throw new RegexInvalidException(
					"Der angegebene reguläre Ausdruck darf nur die Grundoperationen enthalten!");
		}
		// Überprüfen, ob der angegebene reguläre Ausdruck mit einem gültigen
		// Zeichen beginnt (um bei weiteren Berechnungen diesen Fall nicht
		// abfangen zu müssen)
		if ((!isCharInAlphabet(regex.charAt(0)))
				&& (!isEscapeMetaCharacter(regex.charAt(0)))
				&& (regex.charAt(0) != '(')) {
			throw new RegexInvalidException(
					"Der angegebene reguläre Ausdruck fängt mit einem ungütligen Zeichen an!");
		}
		// Überprüfen, ob der angegebene reguläre Ausdruck mit einem gültigen
		// Zeichen endet (um bei weiteren Berechnungen diesen Fall nicht
		// abfangen zu müssen)
		if ((!isCharInAlphabet(regex.charAt(regex.length() - 1)))
				&& (regex.charAt(regex.length() - 1) != '*')
				&& (regex.charAt(regex.length() - 2) != '\\')
				&& (regex.charAt(regex.length() - 1) != ')')) {
			throw new RegexInvalidException(
					"Der angegebene reguläre Ausdruck endet mit einem ungütligen Zeichen!");
		}

		// Hier beginnt die "richtige" Verarbeitung
		ArrayList<String> regexTasks = new ArrayList<String>();

		// ArrayList befüllen
		for (int i = 0; i < regex.length(); i++) {
			char c = regex.charAt(i);
			if (isBasicMetaCharacter(c)) {
				if (c == '(') {
					// Der regex enthält bereits einen geklammerten Ausdruck!
					// Geklammerten Regex rekursiv auflösen.
					int toClose = 1;
					StringBuilder subRegex = new StringBuilder();
					while (toClose != 0) {
						i++;
						if (i == regex.length()) {
							throw new RegexInvalidException(
									"Der angegebene reguläre Ausdruck ist ungültig geklammert");
						}
						if (regex.charAt(i) == '(') {
							subRegex.append(regex.charAt(i));
							toClose++;
						} else if (regex.charAt(i) == ')') {
							toClose--;
							if (toClose > 0) {
								subRegex.append(regex.charAt(i));
							}
						} else if (regex.charAt(i) == '\\') {
							if (i + 1 == regex.length()) {
								throw new RegexInvalidException(
										"Der angegebene reguläre Ausdruck ist ungültig geklammert");
							}
							subRegex.append(regex.charAt(i));
							i++;
							subRegex.append(regex.charAt(i));
						} else {
							subRegex.append(regex.charAt(i));
						}
					}
					regexTasks.add(addMissingParenthesis(subRegex.toString()));
				} else if (c == ')') {
					throw new RegexInvalidException(
							"Der angegebene reguläre Ausdruck ist ungültig geklammert");
				} else if (c == '|' || c == '*') {
					regexTasks.add("" + c);
				} else {
					throw new RegexInvalidException(
							"Unbekannter Ausnahmefehler. Fehlercode: f-i1-e");
				}
			} else if (isEscapeMetaCharacter(c)) {
				regexTasks.add("(" + c + "" + regex.charAt(i + 1) + ")");
				i++;
			} else if (isCharInAlphabet(c)) {
				regexTasks.add("(" + c + ")");
			} else {
				throw new RegexInvalidException(
						"Der angegebene reguläre Ausdruck enthält ungültige Zeichen: '"
								+ c + "'");
			}

		}

		// ArrayList abarbeiten, bis nur noch ein eintrag übrig ist.
		if (regexTasks.size() == 0) {
			throw new RegexInvalidException(
					"Unbekannter Ausnahmefehler. Fehlercode: r-0");
		} else {
			// closure
			for (int i = 0; i < regexTasks.size(); i++) {
				if (regexTasks.get(i).equals("*")) {
					regexTasks.set(i - 1, "(" + regexTasks.get(i - 1) + "*)");
					regexTasks.remove(i);
					i--;
				}
			}

			// concat
			for (int i = 0; i < regexTasks.size(); i++) {
				if (i + 1 < regexTasks.size()) {
					// '*' kann nicht mehr kommen, da bereits vollständig
					// abgearbeitet.
					if ((!regexTasks.get(i).equals("|"))
							&& (!regexTasks.get(i + 1).equals("|"))) {
						regexTasks.set(i,
								"(" + regexTasks.get(i) + regexTasks.get(i + 1)
										+ ")");
						regexTasks.remove(i + 1);
						i--;
					}
				}
			}

			// union
			for (int i = 0; i < regexTasks.size(); i++) {
				if (regexTasks.get(i).equals("|")) {
					regexTasks.set(i - 1, "(" + regexTasks.get(i - 1) + "|"
							+ regexTasks.get(i + 1) + ")");
					regexTasks.remove(i + 1);
					regexTasks.remove(i);
					i = i - 2;
				}
			}
			return regexTasks.get(0);
		}
	}

	/**
	 * Gibt an, ob der angegebene reguläre Ausdruck nur die Grundoperationen
	 * enthält.
	 * 
	 * @param regex
	 * @return
	 */
	public static boolean containsOnlyBasicOperations(String regex) {
		for (int i = 0; i < regex.length(); i++) {
			if (isExtendedMetaCharacter(regex.charAt(i))) {
				return false;
			}
			if (isEscapeMetaCharacter(regex.charAt(i))) {
				// Nach einem Escape-Char darf nur ein Meta-Zeichen kommen
				i++;
				if (regex.length() == i) {
					return false;
				} else {
					if (!isMetaCharacter(regex.charAt(i))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Ersetzt jede Escape-Gruppe durch einen äquivalenten minimalen
	 * Regex-Ausdruck.
	 * 
	 * @param regex
	 *            Der zu reduzierende reguläre Ausdruck.
	 * @return Der reduzierte reguläre Ausdruck.
	 * @throws RegexInvalidException
	 *             Wenn die Escape-Gruppe unbekannt ist oder die Escape Gruppe
	 *             in einer eckigen Klammer falsch verwendet wurde.
	 */
	private static String replaceEscapeGroups(String regex)
			throws RegexInvalidException {
		String outputRegex = regex;
		boolean isInSquareBrackets = false;
		
		// Spezifalfall abfangen, wenn Escape-Gruppe in einer eckigen Klammer
		// ist und zusätzlich von einem "-" umgeben ist.
		for (int i = 0; i < regex.length(); i++) {
			char c = outputRegex.charAt(i);
			if (c == '\\') {
				i++;
			}
			if (c == '-')
			{
				if (isInSquareBrackets)
				{
					if (i-2 >= 0)
					{
						if (outputRegex.charAt(i-2) == '\\')
						{
							if (outputRegex.charAt(i-1) != ']' && outputRegex.charAt(i-1) != '[' && outputRegex.charAt(i-1) != '-' && outputRegex.charAt(i-1) != '\\')
							{
								throw new RegexInvalidException("Der angegebene reguläre Ausdruck ist ab folgender Stelle ungültig: '..." + outputRegex.substring(i-2) + "'");
							}
						}
					}
					if (i+2 < outputRegex.length())
					{
						if (outputRegex.charAt(i+1) == '\\')
						{
							if (outputRegex.charAt(+2) != ']' && outputRegex.charAt(i+2) != '[' && outputRegex.charAt(i+2) != '-' && outputRegex.charAt(i+2) != '\\')
							{
								throw new RegexInvalidException("Der angegebene reguläre Ausdruck ist ab folgender Stelle ungültig: '..." + outputRegex.substring(i-2) + "'");
							}
						}
					}
				}
				else
				{
					throw new RegexInvalidException("Der angegebene reguläre Ausdruck ist ungültig, da ein '-' als Operator nur in einer eckigen Klammer zulässig ist");
				}
			} else if (c == '[') {
				isInSquareBrackets = true;
			} else if (c == ']') {
				isInSquareBrackets = false;
			}
		}
		
		// Hier beginnt die richtige Verarbeitung
		isInSquareBrackets = false;
		for (int i = 0; i < regex.length(); i++) {
			char c = outputRegex.charAt(i);
			if (c == '\\') {
				i++;
				if (i == outputRegex.length()) {
					throw new RegexInvalidException(
							"Der reguläre Ausdruck Endet mit einem ungültigen Zeichen: '"
									+ c + "'");
				}
				c = outputRegex.charAt(i);

				if (isInSquareBrackets) {
					// In den eckigen Klammern muss nur [, ], -, \ und ^ escapt
					// werden.
					if (c != ']' && c != '[' && c != '-' && c != '\\'
							&& c != '^') {
						String bracketContent = getEscapeGroupRepresentation(c);
						if (bracketContent.equals("")) {
							throw new RegexInvalidException(
									"Der erweiterte reguläre Operator '\\"
											+ c
											+ "' ist unbekannt oder wird nicht unterstützt!");
						}
						outputRegex = replaceRangeInString(outputRegex, i - 1,
								i + 1, bracketContent);
						i = 0;
						isInSquareBrackets = false;
					}
				} else {
					if (!isMetaCharacter(c)) {
						String bracketContent = getEscapeGroupRepresentation(c);
						if (bracketContent.equals("")) {
							throw new RegexInvalidException(
									"Der erweiterte reguläre Operator '\\"
											+ c
											+ "' ist unbekannt oder wird nicht unterstützt!");
						}
						outputRegex = replaceRangeInString(outputRegex, i - 1,
								i + 1, "[" + bracketContent + "]");
						i = 0;
						isInSquareBrackets = false;
					}
				}

			} else if (c == '[') {
				isInSquareBrackets = true;
			} else if (c == ']') {
				isInSquareBrackets = false;
			}
		}
		return outputRegex;
	}

	/**
	 * Gibt die Repräsentation einer Escape-Gruppe zurück.
	 * 
	 * @param c
	 *            Der Buchstabe der Gruppe.
	 * @return Die Repräsentation der Escape-Gruppe. Wenn die Gruppe unbekannt
	 *         ist wird der ein leerer String zurückgegeben. Die Rückgabe
	 *         erfolgt ohne die eckigen Klammern.
	 */
	public static String getEscapeGroupRepresentation(char c) {
		String representation = "";
		if (c == 'w') // Alphanumeric characters plus "_"
		{
			representation = "A-Za-z0-9_";
		} else if (c == 'W') // Non-word characters
		{
			representation = "^A-Za-z0-9_";
		} else if (c == 'd') // Digits
		{
			representation = "0-9";
		} else if (c == 'D') // Non-digits
		{
			representation = "^0-9";
		}
		return representation;
	}

	/**
	 * Ersetzt jeden Punkt-Operator (".") durch einen äquivalenten minimalen
	 * Regex-Ausdruck.
	 * 
	 * @param regex
	 *            Der zu reduzierende reguläre Ausdruck.
	 * @return Der reduzierte reguläre Ausdruck.
	 */
	private static String replaceDots(String regex) {
		String outputRegex = regex;
		StringBuilder sb = new StringBuilder("(");

		for (int i = FIRST_ASCII_CHAR; i <= LAST_ASCII_CHAR; i++) {
			char c = (char) i;

			if (isMetaCharacter(c)) {
				sb.append("|\\" + c);
			} else {
				sb.append("|" + c);
			}
		}
		sb.append(")");
		sb.delete(1, 2); // erstes "|" entfernen.

		String replaceWith = sb.toString();

		boolean replace = true;
		for (int i = 0; i < outputRegex.length(); i++) {
			char c = outputRegex.charAt(i);
			if (c == '\\') {
				// Dadurch wird z.B. "\." ignoriert.
				i++;
			} else if (c == '[') {
				// In eckigen Klammern hat der Punkt keine bedeutung.
				replace = false;
			} else if (c == ']') {
				replace = true;
			} else if (c == '.') {
				if (replace) {
					outputRegex = replaceRangeInString(outputRegex, i, i + 1,
							replaceWith);
					i = -1;
				}
			}
		}
		return outputRegex;
	}

	// /**
	// * Ersetzt jedes vorkommen von "[...]" mit einem entsprechenden
	// reduzierten
	// * regulären Ausdruck: Fall1: [first] wird ersetz durch (f|i|r|s|t).
	// Fall2:
	// * [a-d] wird ersetzt durch (a|b|c|d). Fall3: [a-dA-D] wird ersetzt durch
	// * (a|b|c|d|A|B|C|D). Fall4: [^a] wird ersetzt durch eine
	// (...|...|...|...)
	// * (jedes Zeichen aus dem Alphabet außer dem angegebenen Zeichen oder
	// einem
	// * der reservierten Zeichen. Fall5: [-a-c], [a-c-] wird ersetzt durch
	// * (-|a|b|c) bzw. (a|b|c|-).
	// *
	// * @param regex
	// * @return
	// * @throws RegexInvalidException
	// */
	// private static String replaceBrackets(String regex)
	// throws RegexInvalidException {
	// // TODO: replaceBrackets implementieren.
	// return regex;
	// }

	/**
	 * Gibt an ob es sich bei dem angegebenen Zeichen um ein Metazeichen
	 * handelt.
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn es sich um ein Metazeichen handelt, sonst false.
	 */
	protected static boolean isMetaCharacter(char c) {
		return isBasicMetaCharacter(c) || isExtendedMetaCharacter(c)
				|| isEscapeMetaCharacter(c);
	}

	/**
	 * Gibt an ob es sich bei dem angegebenen Zeichen um ein Basis-Metazeichen
	 * handelt.
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn es sich um ein Basis-Metazeichen handelt, sonst false.
	 */
	protected static boolean isBasicMetaCharacter(char c) {
		for (char rc : BASIC_META_CHARS) {
			if (rc == c) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gibt an ob es sich bei dem angegebenen Zeichen um ein erweitertes
	 * Metazeichen handelt.
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn es sich um ein erweitertes Metazeichen handelt, sonst
	 *         false.
	 */
	protected static boolean isExtendedMetaCharacter(char c) {
		for (char rc : EXTENDED_META_CHARS) {
			if (rc == c) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gibt an ob es sich bei dem angegebenen Zeichen um ein Escape-Metazeichen
	 * handelt.
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn es sich um ein Escape-Metazeichen handelt, sonst
	 *         false.
	 */
	protected static boolean isEscapeMetaCharacter(char c) {
		if (ESCAPE_META_CHAR == c) {
			return true;
		}

		return false;
	}

	/**
	 * Gibt an ob das angegebene Zeichen Teil des Alphabets ist und kein
	 * Metazeichen ist.
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn das engegebene Zeichen im Alphabets liegt und kein
	 *         Metazeichen ist.
	 */
	protected static boolean isCharInAlphabet(char c) {
		return c >= FIRST_ASCII_CHAR && c <= LAST_ASCII_CHAR
				&& (!isMetaCharacter(c));
	}

	/**
	 * Gibt an ob das angebene Zeichen Teil des Alphabets ist oder ein
	 * Metazeichen.
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn das engegebene Zeichen im Alphabets liegt oder ein
	 *         Metazeichen ist.
	 */
	protected static boolean isValidChar(char c) {
		return isCharInAlphabet(c) || isMetaCharacter(c);
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
	private static String replaceRangeInString(String inputString,
			int beginIndex, int endIndex, String replaceString) {
		return inputString.substring(0, beginIndex) + replaceString
				+ inputString.substring(endIndex);
	}
}
