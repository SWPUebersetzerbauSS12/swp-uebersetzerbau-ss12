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

package de.fuberlin.bii.bufferedreader;

/**
 * 
 * Die Klasse SpecialChars stellt Sonderzeichen als Konstanten mit zugehörigen Testfunktionen bereit.
 * 
 * @author Johannes Dahlke
 *
 */
public class SpecialChars {

	/**
	 * Zeilenende
	 */
	public static final char CHAR_EOF = 0x1A;
	
	/**
	 * Leerzeichen
	 */
	public static final char CHAR_SPACE = 0x20;
	
	/**
	 * Seitenvorschub
	 */
	public static final char CHAR_FORM_FEED = 0x0C;
	
	/**
	 * Zeilenvorschub
	 */
	public static final char CHAR_LINE_FEED = 0x0A;
	
	/**
	 * Wagenrücklauf
	 */
	public static final char CHAR_CARRIAGE_RETURN = 0x0D;
	
	/**
	 * Hoizontaler Tabulator
	 */
	public static final char CHAR_HORIZONTAL_TAB = 0x09;
	
	/**
	 * Vertikaler Tabulator
	 */
	public static final char CHAR_VERTICAL_TAB = 0x0B;
	
	
	/**
	 * Test auf Sonderzeichen.
	 * @param c Das Zeichen
	 * @return True, wenn c ein Sonderzeichen ist, sonst False
	 */
	public static boolean isSpecialChar( char c) {
		switch ( c) {
			case CHAR_EOF :
			case CHAR_SPACE : 
			case CHAR_FORM_FEED :
			case CHAR_LINE_FEED :
			case CHAR_CARRIAGE_RETURN : 
			case CHAR_HORIZONTAL_TAB : 
			case CHAR_VERTICAL_TAB :
				return true; 
			default : 
				return false;			
		}
	}
	
	
  /**
   * Test auf typografischen Weißraum.
   * @param c Das zu bewertende Zeichen.
   * @return True, wenn c ein Leeraum darstellt, sonst False.
   */
	public static boolean isWhiteSpace( char c) {
		switch ( c) {
			case CHAR_SPACE : 
			case CHAR_FORM_FEED :
			case CHAR_LINE_FEED :
			case CHAR_CARRIAGE_RETURN : 
			case CHAR_HORIZONTAL_TAB : 
			case CHAR_VERTICAL_TAB :
				return true; 
			default : 
				return false;			
		}
	}
 
	
	/**
	 * Test auf Zeilenumbruch.
	 * @param c das zu testende Zeichen.
	 * @return True, wenn c ein Zeileumbruch ist, sonst False.
	 */
	public static boolean isNewLine( char c) {
		switch ( c) {
			case CHAR_FORM_FEED :
			case CHAR_LINE_FEED :
			case CHAR_CARRIAGE_RETURN : 
				return true; 
			default : 
				return false;			
		}
	}
	
	/**
	 * Test auf Zeilenende.
	 * @param c Das zu prüfende Zeichen.
	 *
	 * @return True, wenn c das Zeilenende markiert, sonst False. 
	 */
	public static boolean isEOF( char c) {
		return CHAR_EOF == c;
	}
	
 
}
