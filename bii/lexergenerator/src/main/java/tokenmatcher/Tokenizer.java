package tokenmatcher;

import bufferedreader.EndOfFileException;
import bufferedreader.LexemeReader;
import bufferedreader.SpecialChars;


public class Tokenizer implements LexerToParserInterface {
	
	private DeterministicFiniteAutomata<Character, StatePayload> dfa;
	
	private LexemeReader lexemeReader;
	
	private int newLines = 0;
	
	
	public Tokenizer( LexemeReader lexemeReader, DeterministicFiniteAutomata<Character, StatePayload> dfa) throws Exception {
		super();
		this.dfa = dfa;
		this.lexemeReader = lexemeReader;
	}
	
	

	public Token getNextToken() throws EndOfFileException {
		Character currentChar;
		String currentLexem = "";
		int preliminaryNewLines = 0;
		
		try {
			while ( ( currentChar = lexemeReader.getNextChar()) != SpecialChars.CHAR_EOF) {
				
				// handle white spaces
				if ( SpecialChars.isWhiteSpace( currentChar)) {
					if ( SpecialChars.isNewLine( currentChar)) {
						// count newlines
						// bei windowssystemen muss dann am Ende durch 2 geteilt werden , wegen \r\n
						preliminaryNewLines++;
					}
					// skip whitespaces
					continue;
				}
				
				
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
		  			// reset local newline counter.
		  			newLines += preliminaryNewLines;
		  			preliminaryNewLines = 0;
		  			
		  			return recognisedToken;
		  		}
		  			
		  	} else {
		  		
		  		// TODO: Fehlerbehandlung implementieren 
		  		throw new LexemIdentificationException( String.format( "Cannot assign lexem %s in line %d  to a token.", currentLexem, newLines + preliminaryNewLines));
		  	}	
		  }
		} catch( Exception e) {
			
		}
		throw new EndOfFileException();
		
	}
	

	

}
