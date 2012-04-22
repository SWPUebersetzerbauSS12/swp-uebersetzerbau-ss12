package tokenmatcher;

import bufferedreader.EndOfFileException;
import bufferedreader.LexemeReader;
import bufferedreader.LexemeReaderException;
import bufferedreader.SpecialChars;


public class Tokenizer implements LexerToParserInterface {
	
	private DeterministicFiniteAutomata<Character, StatePayload> dfa;
	
	private LexemeReader lexemeReader;
	
	private int newLines = 1;
	
	
	public Tokenizer( LexemeReader lexemeReader, DeterministicFiniteAutomata<Character, StatePayload> dfa) throws Exception {
		super();
		this.dfa = dfa;
		this.lexemeReader = lexemeReader;
	}
	
	

	public Token getNextToken() throws EndOfFileException, LexemeReaderException, LexemIdentificationException {
		Character currentChar;
		String currentLexem = "";
		int preliminaryNewLines = 0;
		
		dfa.resetToInitialState();
		
			while (true) {
				currentChar = lexemeReader.getNextChar(); 
				
				// handle white spaces
				if ( currentLexem.isEmpty() 
						// Nur wenn nicht bereits ein Lexem verarbeitet wird. 
						// Soll ermöglichen, dass auch ein Zeichen über das Ende deszu lesenen Lexem 
						// gelesen werden kann, auch wenn es ein whitespace ist.
						&& SpecialChars.isWhiteSpace( currentChar)) {
				  
					// count newlines
					if ( SpecialChars.isNewLine( currentChar)) {
						// bei windowssystemen muss dann am Ende durch 2 geteilt werden , wegen \r\n
						preliminaryNewLines++;
					}
					
					// skip whitespaces
					continue;
				}
				
				// hier, damit ein fehlerhaftes Lexem auch in der Fehlermeldung erscheint
				currentLexem += currentChar;
				
		  	if ( dfa.canChangeStateByElement( currentChar)) {
		  		
		  		dfa.changeStateByElement( currentChar);
		  		
		  		if ( dfa.getCurrentState().isFiniteState()) {
		  			StatePayload payload = dfa.getCurrentState().getPayload();
		  			
		  			// Korregieren, was zuviel gelesen wurde.
		  			currentLexem = currentLexem.substring( 0, currentLexem.length() - payload.getBacksteps());
		  			lexemeReader.stepBackward( payload.getBacksteps());
		  			
		  			// Token erstellen
		  			TokenType tokenType = payload.getTokenType();
		  			Token recognisedToken = new Token( tokenType, currentLexem);
		  			
		  			// gelesenenes Lexem akzeptieren
		  			lexemeReader.accept();
		  			// accumulate newlines from local counter.
		  			newLines += preliminaryNewLines;
		  			
		  			return recognisedToken;
		  		}
		  			
		  	} else {
		  		
		  		// TODO: Fehlerbehandlung implementieren 
		  		throw new LexemIdentificationException( String.format( "Cannot assign lexem %s in line %d  to a token.", currentLexem, newLines + preliminaryNewLines));
		  	}	
		  	
		  	if ( currentChar == SpecialChars.CHAR_EOF) 
		  		throw new EndOfFileException();
		 }
		
	}
	

	

}
