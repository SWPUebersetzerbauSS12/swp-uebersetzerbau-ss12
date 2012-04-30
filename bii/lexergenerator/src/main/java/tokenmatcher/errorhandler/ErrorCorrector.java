package tokenmatcher.errorhandler;

import java.util.Collection;

import lexergen.Settings;

import tokenmatcher.DeterministicFiniteAutomata;
import tokenmatcher.LexemIdentificationException;
import tokenmatcher.StatePayload;
import utils.Notification;
import bufferedreader.LexemeReader;
import bufferedreader.LexemeReaderException;
import bufferedreader.SpecialChars;


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


	private void handleMismatchInPanicMode( Character currentChar,
			LexemeReader lexemeReader,
			DeterministicFiniteAutomata<Character, StatePayload> dfa, int lineNumber,
			int positionInLine) throws LexemeReaderException {

		int skippedChars = 0;
		dfa.resetToInitialState();
		while ( !dfa.canChangeStateByElement( currentChar)) {
			currentChar = lexemeReader.getNextChar();
			if ( SpecialChars.isWhiteSpace( currentChar))
				continue;
			skippedChars++;
		}

		lexemeReader.stepBackward( 1);
		lexemeReader.accept();

		Notification.printMismatchMessage( String.format(
					"%d characters skipped at line %d at position %d.",
					skippedChars, lineNumber, positionInLine));
	}


	private void handleMismatchOnPhraseLevel( Character currentChar,
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
	 * @throws LexemeReaderException
	 */
	public void handleMismatch( Character currentChar, LexemeReader lexemeReader,
			DeterministicFiniteAutomata<Character, StatePayload> dfa, int lineNumber,
			int positionInLine) throws LexemeReaderException {
		if ( correctionMode == CorrectionMode.PANIC_MODE)
			handleMismatchInPanicMode( currentChar, lexemeReader, dfa, lineNumber, positionInLine);
		else
			handleMismatchOnPhraseLevel( currentChar, lexemeReader, dfa, lineNumber, positionInLine);
	}

}
