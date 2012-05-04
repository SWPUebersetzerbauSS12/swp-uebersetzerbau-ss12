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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Stellt grundlegende Funktionen zum Arbeiten mit regulären Ausdrücken bereit.
 * 
 * @author Daniel Rotar
 * 
 */
public class Regex {
	/**
	 * Die Metazeichen.
	 */
	private static char[] META_CHARS = { '[', ']', '(', ')', '{', '}', '|',
			'?', '+', '-', '*', '^', '$', '\\', '.' };

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
	 * klammert diesen anschließend korrekt und vollständig.
	 * 
	 * @param regex
	 *            Der zu reduzierende und zu klammernde reguläre Ausdruck.
	 * @return Ein äquivalenter reduzierter und geklammerte regulärer Ausdruck.
	 * @throws RegexInvalidException
	 *             Wenn der angegebene regulärer Ausdruck ungültig ist oder
	 *             nicht unterstützt wird.
	 */
	public static String reduceAndBracketRegex(String regex)
			throws RegexInvalidException {
		return bracketRegex(reduceRegex(regex));
	}

	/**
	 * Reduziert den angebenen regulären Ausdruck auf die Grundoperationen.
	 * 
	 * @param regex
	 *            Der zu reduzierende reguläre Ausdruck.
	 * @return Ein äquivalenter reduzierter regulärer Ausdruck.
	 * @throws RegexInvalidException
	 *             Wenn der angegebene regulärer Ausdruck ungültig ist oder
	 *             nicht unterstützt wird.
	 */
	public static String reduceRegex(String regex) throws RegexInvalidException {
		// Alle Chars überprüfen, ob sie Teil des Alphabets oder ein Metazeichen
		// sind.
		for (int i = 0; i < regex.length(); i++) {
			if (!isMetaOrAlphaChar(regex.charAt(i))) {
				throw new RegexInvalidException(
						"Der angegebene reguläre Ausdruck enthält ein ungültiges Zeichen: '"
								+ regex.charAt(i) + "'!");
			}
		}

		// Überprüfen ob der angegebene Regex gültig ist
		try {
			Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			throw new RegexInvalidException(
					"Der angegebene reguläre Ausdruck '" + regex
							+ "' ist ungültig! ");
		}

		// Verarbeitung starten
		// TODO: Verhalten bei () überprüfen.
		String output = regex;
		// 1. Eckige Klammern reduzieren.
		output = replaceSquareBrackets(output);
		// 2. Punkte reduzieren (muss nach jeden Fall nach (1) gemacht werden).
		output = replaceDots(output);
		// 3. Backshlashes reduzieren (muss nach jeden Fall nach (1) gemacht
		// werden).
		output = replaceBackslashes(output);

		return output;
	}

	/**
	 * Klammert den angebenen regulären Ausdruck korrekt und vollständig.
	 * 
	 * @param regex
	 *            Der zu klammernde reguläre Ausdruck (darf nur die
	 *            Grundoperationen A|B, AB und A* enthalten).
	 * @return Ein äquivalenter geklammerte regulärer Ausdruck.
	 * @throws RegexInvalidException
	 *             Wenn der angegebene regulärer Ausdruck ungültig ist oder
	 *             nicht unterstützt wird.
	 */
	protected static String bracketRegex(String regex)
			throws RegexInvalidException {
		if (regex.length() == 0) {
			return "";
		}

		if (!containsOnlyBasicOperations(regex)) {
			throw new RegexInvalidException(
					"Der angegebene reguläre Ausdruck darf nur die Grundoperationen enthalten!");
		}

		// ArrayList erstellen
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
					regexTasks.add(bracketRegex(subRegex.toString()));
				} else if (c == ')') {
					throw new RegexInvalidException(
							"Der angegebene reguläre Ausdruck ist ungültig geklammert");
				} else if (c == '|' || c == '*') {
					regexTasks.add("" + c);
				} else {
					throw new RegexInvalidException(
							"Unbekannter Ausnahmefehler. Fehlercode: f-i1-e");
				}
			} else if (c == '\\') {
				regexTasks.add("(" + c + "" + regex.charAt(i + 1) + ")");
				i++;
			} else if (isAlphaChar(c)) {
				regexTasks.add("(" + c + ")");
			} else {
				throw new RegexInvalidException(
						"Der angegebene reguläre Ausdruck enthält ungültige Zeichen: '"
								+ c + "'");
			}

		}

		// ArrayList abarbeiten, bis nur noch ein Eintrag übrig ist.
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
	 *            Der zu überprüfende reguläre Ausdruck.
	 * @return true, wenn der reguläre Ausdruck nur die Grundoperationen
	 *         enthält, sonst false.
	 */
	public static boolean containsOnlyBasicOperations(String regex) {
		for (int i = 0; i < regex.length(); i++) {
			if (isExtendedMetaCharacter(regex.charAt(i))) {
				return false;
			}
			if (regex.charAt(i) == '\\') {
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
	 * Reduziert im angebenen regulären Ausdruck alle eckigen Klammern auf die
	 * Grundoperationen.
	 * 
	 * @param regex
	 *            Der zu reduzierende reguläre Ausdruck.
	 * @return Ein äquivalenter reduzierter Ausdruck.
	 */
	private static String replaceSquareBrackets(String regex) {
		String output = regex;

		Pattern pattern = Pattern.compile("[^\\\\]\\[.*[^\\\\]\\]"); // [^\\]\[.*[^\\]\]
		Matcher matcher = pattern.matcher(output);
		while (matcher.find()) {
			String match = matcher.group();
			String subRegex = match.substring(1);
			StringBuilder sb = new StringBuilder("(");
			for (int i = FIRST_ASCII_CHAR; i <= LAST_ASCII_CHAR; i++) {
				String toCeck = "" + ((char) i);
				if (toCeck.matches(subRegex)) {
					if (isMetaCharacter((char) i)) {
						sb.append("|\\" + toCeck);
					} else {
						sb.append("|" + toCeck);
					}
				}
			}
			sb.append(")");
			if (sb.length() > 2) {
				// Wenn nicht "()"
				sb.delete(1, 2); // erstes "|" entfernen.
			}

			output = output.replace(match,
					match.substring(0, 1) + sb.toString());
		}

		return output;
	}

	/**
	 * Reduziert im angebenen regulären Ausdruck alle Punkte auf die
	 * Grundoperationen.
	 * 
	 * @param regex
	 *            Der zu reduzierende reguläre Ausdruck.
	 * @return Ein äquivalenter reduzierter Ausdruck.
	 */
	private static String replaceDots(String regex) {
		String output = regex;

		Pattern pattern = Pattern.compile("[^\\\\]\\."); // [^\\]\.
		Matcher matcher = pattern.matcher(output);
		while (matcher.find()) {
			String match = matcher.group();
			String subRegex = match.substring(1);
			StringBuilder sb = new StringBuilder("(");
			for (int i = FIRST_ASCII_CHAR; i <= LAST_ASCII_CHAR; i++) {
				String toCeck = "" + ((char) i);
				if (toCeck.matches(subRegex)) {
					if (isMetaCharacter((char) i)) {
						sb.append("|\\" + toCeck);
					} else {
						sb.append("|" + toCeck);
					}
				}
			}
			sb.append(")");
			if (sb.length() > 2) {
				// Wenn nicht "()"
				sb.delete(1, 2); // erstes "|" entfernen.
			}

			output = output.replace(match,
					match.substring(0, 1) + sb.toString());
		}

		return output;
	}

	private static String replaceBackslashes(String regex) {
		String output = regex;

		for (int i = 0; i < output.length(); i++) {
			if (output.charAt(i) == '\\') {
				if (i + 1 < output.length()) {
					String subRegex = "" + output.charAt(i)
							+ output.charAt(i + 1);
					StringBuilder sb = new StringBuilder("(");
					for (int j = FIRST_ASCII_CHAR; j <= LAST_ASCII_CHAR; j++) {
						String toCeck = "" + ((char) j);
						if (toCeck.matches(subRegex)) {
							if (isMetaCharacter((char) j)) {
								sb.append("|\\" + toCeck);
							} else {
								sb.append("|" + toCeck);
							}
						}
					}
					sb.append(")");
					if (sb.length() > 2) {
						// Wenn nicht "()"
						sb.delete(1, 2); // erstes "|" entfernen.
					}

					output = replaceRangeInString(output, i, i + 2,
							sb.toString());
				}
				i++;
			}
		}

		return output;
	}

	/**
	 * Gibt an, ob es sich bei dem angegebenen Zeichen um ein Metazeichen
	 * handelt.
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn es sich um ein Metazeichen handelt, sonst false.
	 */
	protected static boolean isMetaCharacter(char c) {
		for (char mc : META_CHARS) {
			if (mc == c) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gibt an ob das angebene Zeichen zum Alphabet (und nicht zu den
	 * Metazeichen) gehört.
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn das angegebene Zeichen zum Alphabet (und nicht zu den
	 *         Metazeichen) gehört, sonst false.
	 */
	protected static boolean isAlphaChar(char c) {
		return c >= FIRST_ASCII_CHAR && c <= LAST_ASCII_CHAR
				&& (!isMetaCharacter(c));
	}

	/**
	 * Gibt an ob das angebene Zeichen zum Alphabet oder zu den Metazeichen
	 * gehört
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn das angegebene Zeichen zum Alphabet oder zu den
	 *         Metazeichen gehört, sonst false.
	 */
	protected static boolean isMetaOrAlphaChar(char c) {
		return isAlphaChar(c) || isMetaCharacter(c);
	}

	/**
	 * Gibt an ob es sich bei dem angegebenen Zeichen um ein erweitertes
	 * Metazeichen handelt.
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn es sich um eins der nachfolgenden Metazeichen handelt
	 *         '[', ']', '{', '}', '?', '+', '-', '^', '$', '.', sonst false.
	 */
	private static boolean isExtendedMetaCharacter(char c) {
		char[] extendedMetaChars = { '[', ']', '{', '}', '?', '+', '-', '^',
				'$', '.' };
		for (char rc : extendedMetaChars) {
			if (rc == c) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gibt an ob es sich bei dem angegebenen Zeichen um ein Basis-Metazeichen
	 * handelt.
	 * 
	 * @param c
	 *            Das zu überprüfende Zeichen
	 * @return true, wenn es sich um eins der nachfolgenden Metazeichen handelt
	 *         '(', ')', '|', '*', sonst false.
	 */
	private static boolean isBasicMetaCharacter(char c) {
		char[] basicMetaChars = { '(', ')', '|', '*' };
		for (char rc : basicMetaChars) {
			if (rc == c) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Ersetzt in dem angegebenen String den angegebenen Bereich mit dem
	 * angegebenen Inhalt.
	 * 
	 * @param inputString
	 *            Der String, in dem der Bereich ersetzt werden soll.
	 * @param beginIndex
	 *            Der Start-Index des Bereichs, der ersetzt werden soll.
	 * @param endIndex
	 *            Der End-Index des Bereichs, der ersetzt werden soll.
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
