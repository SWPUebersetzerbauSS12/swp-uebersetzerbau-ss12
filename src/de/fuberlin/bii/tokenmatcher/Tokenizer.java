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

package de.fuberlin.bii.tokenmatcher;

import de.fuberlin.bii.tokenmatcher.attributes.Attribute;
import de.fuberlin.bii.tokenmatcher.errorhandler.ErrorCorrector;
import de.fuberlin.bii.tokenmatcher.errorhandler.ErrorCorrectorException;
import de.fuberlin.bii.utils.Notification;
import de.fuberlin.bii.bufferedreader.LexemeReader;
import de.fuberlin.bii.bufferedreader.LexemeReaderException;
import de.fuberlin.bii.bufferedreader.SpecialChars;

/**
 * Der Tokenizer implementiert die {@link LexerToParserInterface}-Schnittstelle, über welche der Parser Token anfordern kann.
 * Auf Anforderung eines Token reagiert der Tokenizer seinerseits durch zeichenweises Anfordern der Eingabe und speist damit einen deterministischen endlichen Automaten. 
 * Akzeptiert der DEA die Eingabe, so liefert der Tokenizer den erkannten Token, anderenfalls leitet der Tokenizer eine Fehlerbahandlung nach gewählten Fehlerkorrekturmodus ein.
 * 
 * @author Johannes Dahlke
 *
 */
public class Tokenizer implements LexerToParserInterface {

	private DeterministicFiniteAutomata<Character, StatePayload> dfa;

	private LexemeReader lexemeReader;
	private ErrorCorrector errorCorrector;
	
	private int currentLine = 1;
	private int currentPositionInLine = 0;
	private int lastLine = 1;
	private int lastPositionInLine = 0; 
	
	private enum ReadMode {
		READ_NORMAL,
		READ_BLOCK_COMMENT,
    READ_LINE_COMMENT
	}
	
	private ReadMode readMode = ReadMode.READ_NORMAL;
	
	public Tokenizer( LexemeReader lexemeReader,
			DeterministicFiniteAutomata<Character, StatePayload> dfa)
			throws Exception {
		super();
		this.dfa = dfa;
		this.lexemeReader = lexemeReader;
		errorCorrector = new ErrorCorrector( ErrorCorrector.CorrectionMode.PANIC_MODE);//Settings.getErrorCorrectionMode());
	}
	
	
  public Token getNextToken() throws LexemeReaderException,
			LexemIdentificationException {
		Character currentChar;
		String currentLexem = "";
		
		dfa.resetToInitialState();

		boolean eofReached = false;
		while ( !eofReached) {
			currentChar = lexemeReader.getNextChar();
      currentPositionInLine++;
			
			// handle white spaces
			if ( currentLexem.isEmpty()
			// Nur wenn nicht bereits ein Lexem verarbeitet wird.
			// Soll ermöglichen, dass auch ein Zeichen über das Ende des zu lesenden
			// Lexem gelesen werden kann, auch wenn es ein whitespace ist.
					&& SpecialChars.isWhiteSpace( currentChar)) {

				// count newlines
				if( SpecialChars.isNewLine( currentChar)) {
				  // handle \r\n for windows systems					
					if ( currentChar == SpecialChars.CHAR_CARRIAGE_RETURN) {
						if ( lexemeReader.getNextChar() != SpecialChars.CHAR_LINE_FEED)
							lexemeReader.stepBackward( 1);     
					}	
					currentLine++;
					currentPositionInLine = 0;
				}

				// skip whitespaces
				continue;
			}

			// if we read EOF and there is no lexem left
			if ( SpecialChars.isEOF( currentChar) 
					&& currentLexem.isEmpty()) {
				// then skip
				eofReached = true;
				break;
			}
		  
			
			if ( dfa.canChangeStateByElement( currentChar)) {
				currentLexem += currentChar;
				dfa.changeStateByElement( currentChar);
				// TODO if ( dfa.getCurrentState().isFiniteState()) then remember in this possible match  (error handling aspect)
			} else if ( !currentLexem.isEmpty() 
					&& dfa.getCurrentState().isFiniteState()) {
				
				StatePayload payload = dfa.getCurrentState().getPayload();

				// Lesezeiger zurücksetzen um das, was zuviel gelesen wurde.
				// In dieser implementierung immer 1 Zeichen
				if ( currentChar != SpecialChars.CHAR_EOF)
				  lexemeReader.stepBackward( 1);

				// Token erstellen
				String tokenType = payload.getTokenType();
				Attribute attribute = payload.getAttribute();
				Object attributeValue = attribute.lexemToValue( currentLexem);

				Token recognisedToken = new Token( tokenType, attributeValue, currentLine, currentPositionInLine);

				// gelesenenes Lexem akzeptieren
				lexemeReader.accept();
				// update position counter
				lastLine = currentLine;
				lastPositionInLine = currentPositionInLine;
				
        // Fehlerbehandler rücksetzen
				errorCorrector.reset();
				
				// filter comments
				if ( ( readMode == ReadMode.READ_NORMAL) &&
						 ( Token.isTokenStartingBlockComment( recognisedToken))) {
					readMode = ReadMode.READ_BLOCK_COMMENT;
					while ( !Token.isTokenEndingBlockComment( getNextToken())){
						// ignore comment block
					}
					readMode = ReadMode.READ_NORMAL;
					return getNextToken();
				} else if ( ( readMode == ReadMode.READ_NORMAL) &&
						        ( Token.isTokenLineComment( recognisedToken))) {
					readMode = ReadMode.READ_LINE_COMMENT;
					int thisLine = currentLine;
					while ( thisLine == currentLine){
						// ignore remaining line
						recognisedToken = getNextToken();
						if ( recognisedToken.isEofToken()) {
							eofReached = true;
							break;
						}
					} 
					readMode = ReadMode.READ_NORMAL;
					return recognisedToken;
				} else
				  return recognisedToken;
				
			} else if ( readMode == ReadMode.READ_NORMAL){
				// error handling
				String mismatchMessage = "";
				try {
				  mismatchMessage = errorCorrector.handleMismatch( currentChar, lexemeReader, dfa, currentLine, currentPositionInLine);
				} catch ( ErrorCorrectorException e) {
				  // then skip
					Notification.printMismatchMessage( String.format(
							"Cannot resolve lexem '%s'. Abort lexing.", currentLexem));
					eofReached = true;
					break;
				}
				// Otherwise, the error corrector has found a solution that solve the problem. 
				// But first, we let the user know about the conflict by throwing an exception
				Notification.printMismatchMessage( mismatchMessage);
				throw new LexemIdentificationException( mismatchMessage);
		  } else {
		  	// ignore, cause we scan a comment at the moment
		  }
		}
		
		return Token.getEofToken();
	}


	public void reset() throws LexemeReaderException {
		dfa.resetToInitialState();
		lexemeReader.reopen();
	}

}
