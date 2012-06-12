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

package tokenmatcher.errorhandler;

import java.util.Collection;

import tokenmatcher.DeterministicFiniteAutomata;
import tokenmatcher.LexemIdentificationException;
import tokenmatcher.StatePayload;
import utils.Notification;
import bufferedreader.LexemeReader;
import bufferedreader.LexemeReaderException;
import bufferedreader.SpecialChars;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public class ErrorCorrector {

	public enum CorrectionMode {
		PANIC_MODE, PHRASE_LEVEL
	}

	private enum Heuristic {
		REMOVE_CHAR, ADD_CHAR, REPLACE_CHAR, TOGGLE_TWO_CHARS
	}

	private CorrectionMode correctionMode = CorrectionMode.PANIC_MODE; // default

	private Heuristic currentHeuristic;
	
	private int lastLineNumber = 0;
	private int lastPositionInLine = 0;

	private char currentReplacement;


	public ErrorCorrector( CorrectionMode correctionMode) {
		super();
		this.correctionMode = correctionMode;
		reset();
	}


	/**
	 * Setzt die als nächstes zu verwendende Heuristik auf den Ausgangswert
	 * zurück.
	 */
	public void reset() {
		currentHeuristic = Heuristic.REMOVE_CHAR;
		// TODO: ensure excludion of special chars like eof
		currentReplacement = 0x00;
	}


	private String handleMismatchInPanicMode( Character currentChar,
			LexemeReader lexemeReader,
			DeterministicFiniteAutomata<Character, StatePayload> dfa, int lineNumber,
			int positionInLine) throws ErrorCorrectorException, LexemeReaderException {

		
		int skippedChars = 0;
		dfa.resetToInitialState();
		while ( !dfa.canChangeStateByElement( currentChar)) {
			currentChar = lexemeReader.getNextChar();
			if ( SpecialChars.isEOF( currentChar)) {
				throw new ErrorCorrectorException( "Reached end of line without find a solution.");
			}
			if ( SpecialChars.isWhiteSpace( currentChar))
				continue;
			skippedChars++;
		}

		lexemeReader.stepBackward( 1);
		lexemeReader.accept();
		
		String mismatchMessage = String.format(
				"%d characters skipped at line %d at position %d while error correction in panic mode.",
				skippedChars, lineNumber, positionInLine);
		
		return mismatchMessage;
	}


	private String handleMismatchOnPhraseLevel( Character currentChar,
			LexemeReader lexemeReader,
			DeterministicFiniteAutomata<Character, StatePayload> dfa, int lineNumber,
			int positionInLine) {
		
		// reset, if a new error occur
		if ( lastLineNumber != lineNumber
				|| lastPositionInLine != positionInLine)
			reset();
		
		Notification.printErrorMessage( "Mismatch: " + currentChar);
		switch ( currentHeuristic) {
			case REMOVE_CHAR:
					Notification.printMismatchMessage( String.format(
							"Mismatch: character %s skipped at line %d at position %d.",
							currentChar, lineNumber, positionInLine));
					lastLineNumber = lineNumber;
					lastPositionInLine = positionInLine;
					currentHeuristic = Heuristic.ADD_CHAR;
					break;
			case ADD_CHAR:
			    Collection<Character> possiblesChars = dfa.getElementsOfOutgoingTransitionsFromState( dfa.getCurrentState());
			    for ( Character character : possiblesChars) {
			    	// TODO Heuristic add a char
					}
			    break;
		case REPLACE_CHAR:
		      // TODO Heuristic replace a char
			break;
		case TOGGLE_TWO_CHARS:
		      // TODO Heuristic toggle two chars
			break;

		default:
			break;
	  }
		
		String mismatchMessage = "";
		return mismatchMessage;
	}




	/**
	 * Führt die Fehlerbehandlung je nach gewählten Fehlerbehebungsmodus durch.
	 * Der Fehlerbehebungsmodus wird in der {@link Settings} gesetzt.
	 * 
	 * @param currentChar
	 *          das zuletzt gelesene Zeichen
	 * @param lexemeReader
	 *          der Leser der die Eingabe liefert.
	 * @param dfa
	 *          der Automat, über dem der Tokenabgleich erfolgt.
	 * @param lineNumber
	 *          die Zeile, in der der Fehler auftritt.
	 * @param positionInLine
	 *          die Position in der Zeile, in der der Fehler auftritt.
	 *          
	 * @return the mismatch message 
	 * @throws LexemeReaderException
	 * @throws EndOfFileException 
	 */
	public String handleMismatch( Character currentChar, LexemeReader lexemeReader,
			DeterministicFiniteAutomata<Character, StatePayload> dfa, int lineNumber,
			int positionInLine) throws ErrorCorrectorException, LexemeReaderException {
		if ( correctionMode == CorrectionMode.PANIC_MODE)
			return handleMismatchInPanicMode( currentChar, lexemeReader, dfa, lineNumber, positionInLine);
		else
			return handleMismatchOnPhraseLevel( currentChar, lexemeReader, dfa, lineNumber, positionInLine);
	}

}



