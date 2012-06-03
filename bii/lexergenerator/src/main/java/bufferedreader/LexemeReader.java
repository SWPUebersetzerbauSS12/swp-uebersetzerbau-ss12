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

package bufferedreader;


public interface LexemeReader {
	
	/**
	 * Liest das nächste Zeichen von der Eingabe. Am Ende des Eingabestroms 
	 * wird das {@link SpecialChars#CHAR_EOF} geliefert. 
	 * @return das nächste Zeichen.
	 * @throws LexemeReaderException wenn es zu einen IO Fehler kam. 
	 * @throws EndOfFileException 
	 */
	char getNextChar() throws LexemeReaderException;
	
	
	/**
	 * Setzt den Positionszeiger des Lesers hinter das Ende des 
	 * zuletzt akzeptierten Lexems. 
	 * @throws LexemeReaderException 
	 */
	void reset() throws LexemeReaderException;
	
	
	/**
	 * Öffnet die Eingabedatei erneut. Der nächste Aufruf von {@link #getNextChar()} 
	 * liefert dann das erste Zeichen aus der Quelldatei.  
	 * @throws LexemeReaderException 
	 */
	void reopen() throws LexemeReaderException;
	
	/**
	 * Acceptiert das zuletzt gelesene Lexem, indem es den Marker lexemeBegin 
	 * genau hinter dem zuletzt gelesenen Lexem positioniert. 
	 * @throws LexemeReaderException 
	 */
	void accept() throws LexemeReaderException;
	
	/**
	 * Setzt den Zeiger der aktuellen Leseposition um die angegebene Anzahl an Schritten zurück. 
	 * @param steps die Anzahl der Schritte
	 * @throws LexemeReaderException 
	 */
	void stepBackward( int steps) throws LexemeReaderException;
	

}
