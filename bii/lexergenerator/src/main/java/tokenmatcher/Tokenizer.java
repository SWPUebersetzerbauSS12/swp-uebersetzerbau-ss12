package tokenmatcher;

import bufferedreader.EndOfFileException;
import bufferedreader.LexemeReader;
import bufferedreader.LexemeReaderException;
import bufferedreader.SpecialChars;


public class Tokenizer implements LexerToParserInterface {

	private DeterministicFiniteAutomata<Character, StatePayload> dfa;

	private LexemeReader lexemeReader;

	private int currentLine = 1;
	private int currentPositionInLine = 0;
	private int lastLine = 1;
	private int lastPositionInLine = 0;
	


	public Tokenizer( LexemeReader lexemeReader,
			DeterministicFiniteAutomata<Character, StatePayload> dfa)
			throws Exception {
		super();
		this.dfa = dfa;
		this.lexemeReader = lexemeReader;
	}
	
	

	public Token getNextToken() throws EndOfFileException, LexemeReaderException,
			LexemIdentificationException {
		Character currentChar;
		String currentLexem = "";
		
		dfa.resetToInitialState();

		while ( true) {
			currentChar = lexemeReader.getNextChar();
      currentPositionInLine++;
			
			// handle white spaces
			if ( currentLexem.isEmpty()
			// Nur wenn nicht bereits ein Lexem verarbeitet wird.
			// Soll ermöglichen, dass auch ein Zeichen über das Ende des zu lesenden
			// Lexem gelesen werden kann, auch wenn es ein whitespace ist.
					&& SpecialChars.isWhiteSpace( currentChar)) {

				// count newlines
				// bei windowssystemen muss dann am Ende durch 2 geteilt werden ,
				// wegen \r\n
				if( SpecialChars.isNewLine( currentChar)) {
					currentPositionInLine++;
					currentPositionInLine = 0;
				}

				// skip whitespaces
				continue;
			}

			
			if ( dfa.canChangeStateByElement( currentChar)) {
				currentLexem += currentChar;
				dfa.changeStateByElement( currentChar);
			} else if ( dfa.getCurrentState().isFiniteState()) {

				StatePayload payload = dfa.getCurrentState().getPayload();

				// Korregieren, was zuviel gelesen wurde.
				currentLexem = currentLexem.substring( 0, currentLexem.length()
						- payload.getBacksteps());
				lexemeReader.stepBackward( payload.getBacksteps());

				// Token erstellen
				TokenType tokenType = payload.getTokenType();
				Token recognisedToken = new Token( tokenType, currentLexem);

				// gelesenenes Lexem akzeptieren
				lexemeReader.accept();
				// update position counter
				lastLine = currentLine;
				lastPositionInLine = currentPositionInLine;

				return recognisedToken;
			} else if ( currentChar == SpecialChars.CHAR_EOF) {
				throw new EndOfFileException();
		  } else {
        
				// TODO: Fehlerbehandlung implementieren
				throw new LexemIdentificationException( String.format(
						"Cannot assign lexem %s in line %d at position %d to a token.", currentLexem +currentChar,
						currentLine, currentPositionInLine));
			}

			
		}

	}

}
