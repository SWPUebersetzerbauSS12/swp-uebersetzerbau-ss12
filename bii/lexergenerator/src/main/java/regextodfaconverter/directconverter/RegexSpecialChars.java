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

public class RegexSpecialChars {

	public static final char REGEX_ALTERNATIVE_CHAR = '|';
	public static final char REGEX_MASK_CHAR = '\\';
	public static final char REGEX_CLASS_BEGIN = '[';
	public static final char REGEX_CLASS_END = ']';
	public static final char REGEX_REPETITION_BEGIN = '{';
	public static final char REGEX_REPETITION_END = '}';
	public static final char REGEX_GROUP_BEGIN = '(';
	public static final char REGEX_GROUP_END = ')';
	public static final char REGEX_KLEENE_CLOSURE = '*';
	public static final char REGEX_POSITIVE_KLEENE_CLOSURE = '+';
	public static final char REGEX_OPTION = '?';
	public static final char REGEX_JOKER = '.';

	public static final char EMPTY_STRING = 0x00;
	public static final char TERMINATOR = 0x03; // ETX = End Of Text

	/**
	 * Prüft, ob ein Zeichen ein Zeichen mit besonderer Bedeutung bezüglich regulärer Ausdrücke ist.
	 * @param theCharacter
	 * @return
	 */
	public static boolean isSpecialChar( char theCharacter) {
		switch ( theCharacter) {
			case REGEX_MASK_CHAR:
			case REGEX_GROUP_BEGIN:
			case REGEX_GROUP_END:
			case REGEX_ALTERNATIVE_CHAR:
			case REGEX_CLASS_BEGIN:
			case REGEX_CLASS_END:
			case REGEX_REPETITION_BEGIN:
			case REGEX_REPETITION_END:
			case REGEX_KLEENE_CLOSURE:
			case REGEX_POSITIVE_KLEENE_CLOSURE:
			case REGEX_OPTION:
			case REGEX_JOKER:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Ermittelt, ob ein Zeichen zu dem grundlegenden Regex Zeichensatz gehört.
	 * @param theCharacter
	 * @return
	 */
	public static boolean isElementOfBasicCharset( char theCharacter) {
		switch ( theCharacter) {
			case REGEX_MASK_CHAR:
			case REGEX_ALTERNATIVE_CHAR:
			case REGEX_KLEENE_CLOSURE:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isBasicOperator( char theChar) {
		switch ( theChar) {
			case REGEX_ALTERNATIVE_CHAR:
			case REGEX_KLEENE_CLOSURE:
				return true;
			default:
				return false;
		}
	}



	/**
	 * Prüft, ob es sich um ein leeres Wort handelt.
	 * @param theCharacter
	 * @return
	 */
	public static boolean isEmptyString( char theCharacter) {
		return EMPTY_STRING == theCharacter;
	}

}
