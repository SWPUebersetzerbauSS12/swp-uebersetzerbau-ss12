package tokenmatcher.errorhandler;

import tokenmatcher.DeterministicFiniteAutomata;
import tokenmatcher.LexemIdentificationException;
import tokenmatcher.StatePayload;
import bufferedreader.LexemeReader;


public class ErrorCorrector {
	
	public enum CorrectionMode {
		PANIC_MODE,
		PHRASE_LEVEL
	}
	
	private enum Heuristic {
		REMOVE_CHAR,
		ADD_CHAR,
		REPLACE_CHAR,
		TOGGLE_TWO_CHARS
	}
	
	private CorrectionMode correctionMode = CorrectionMode.PANIC_MODE;  // default 
	
	private Heuristic heuristic;
	
	private char currentReplacement;
	
	public ErrorCorrector( CorrectionMode correctionMode) {
		super();
		this.correctionMode = correctionMode;
		reset();
	}

	
	/**
	 * Setzt die als nächstes zu verwendende Heuristik auf den Ausgangswert zurück. 
	 */
	public void reset() {
		heuristic = Heuristic.REMOVE_CHAR;
		// TODO: ensure excludion of special chars like eof
		currentReplacement = 0x00;
	}

	private void handleMismatchInPanicMode( Character currentChar,
			LexemeReader lexemeReader,
			DeterministicFiniteAutomata<Character, StatePayload> dfa) {
		//TODO
		//throw new LexemIdentificationException( "Panic mode not yet implemented!");
	}
	
	private void handleMismatchOnPhraseLevel( Character currentChar,
			LexemeReader lexemeReader,
			DeterministicFiniteAutomata<Character, StatePayload> dfa) {
		//TODO
	//throw new LexemIdentificationException( "Panic mode not yet implemented!");	
	}

	
	/**
	 * Führt die Fehlerbehandlung je nach gewählten Fehlerbehebungsmodus durch.
	 * 
	 * @param currentChar das zuletzt gelesene Zeichen
	 * @param lexemeReader der Leser der die Eingabe liefert.
	 * @param dfa der Automat, über dem der Tokenabgleich erfolgt.
	 */
	public void handleMismatch( Character currentChar,
			LexemeReader lexemeReader,
			DeterministicFiniteAutomata<Character, StatePayload> dfa) {
		if ( correctionMode == CorrectionMode.PANIC_MODE)
			handleMismatchInPanicMode( currentChar, lexemeReader, dfa);
		else 
			handleMismatchOnPhraseLevel( currentChar, lexemeReader, dfa);
	}
	
	
	
	

}
