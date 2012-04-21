package tokenmatcher;

import bufferedreader.EndOfFileException;
import bufferedreader.LexemeReader;
import bufferedreader.SpecialChars;


public class Tokenizer implements LexerToParserInterface {
	
	private DeterministicFiniteAutomata<Character, StatePayload> dfa;
	
	private LexemeReader lexemeReader;
	
	public Tokenizer( LexemeReader lexemeReader, DeterministicFiniteAutomata<Character, StatePayload> dfa) throws Exception {
		super();
		this.dfa = dfa;
		this.lexemeReader = lexemeReader;
	}
	
	

	public Token getNextToken() throws EndOfFileException {
		Character currentChar;
		String currentLexem = "";
		try {
			while ( ( currentChar = lexemeReader.getNextChar()) != SpecialChars.CHAR_EOF) {
		  	if ( dfa.canChangeStateByElement( currentChar)) {
		  		
		  		currentLexem += currentChar;
		  		dfa.changeStateByElement( currentChar);
		  		
		  		if ( dfa.getCurrentState().isFiniteState()) {
		  			StatePayload payload = dfa.getCurrentState().getPayload();
		  			
		  			// Korregieren, was zuviel gelesen wurde.
		  			currentLexem = currentLexem.substring( currentLexem.length() - payload.getBacksteps());
		  			lexemeReader.stepBackward( payload.getBacksteps());
		  			
		  			// Token erstellen
		  			int tokenType = payload.getTokenKind();
		  			Token recognisedToken = new Token( tokenType, currentLexem);
		  			
		  			// gelesenenes Lexem akzeptieren
		  			lexemeReader.accept();
		  			currentLexem = "";
		  			
		  			return recognisedToken;
		  		}
		  			
		  	} else {
		  		
		  		// TODO: Fehlerbehandlung implementieren 
		  		throw new LexemIdentificationException( String.format( "Cannot assign lexem %s to a token.", currentLexem));
		  	}	
		  }
		} catch( Exception e) {
			
		}
		throw new EndOfFileException();
		
	}
	

	

}
