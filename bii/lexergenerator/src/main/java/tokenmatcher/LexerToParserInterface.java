package tokenmatcher;

import bufferedreader.EndOfFileException;
import bufferedreader.LexemeReaderException;

/**
 * Schnittstelle zwischen Lexergenerator und Parsergenerator
 * 
 * @author Johannes Dahlke
 *
 */
public interface LexerToParserInterface {
	
	/**
	 * Liefert den nächsten Token. 
	 * 
	 * @return der nächste Token.
	 * 
	 * @throws EndOfFileException wird geworfen,wenn das Ende der Datei mit dem Quellcode erreicht ist.
	 * @throws LexemeReaderException wird geworfen, wenn der Lexer beim zugriff auf die Quelldatei probleme hat. 
	 * @throws LexemIdentificationException wird geworfen, wenn der Lexer das gelesene Lexem keinem bekannten Tokentyp zuweisen kann.
	 */
	Token getNextToken() throws EndOfFileException, LexemeReaderException, LexemIdentificationException;

}
