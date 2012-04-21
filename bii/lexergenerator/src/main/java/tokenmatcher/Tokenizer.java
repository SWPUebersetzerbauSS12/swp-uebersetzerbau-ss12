package tokenmatcher;

import bufferedreader.LexemeReader;
import bufferedreader.SpecialChars;


public class Tokenizer implements LexerToParserInterface {
	
	private DeterministicFiniteAutomata<Character> dfa;
	
	private LexemeReader lexemeReader;
	
	public Tokenizer( LexemeReader lexemeReader, DeterministicFiniteAutomata<Character> dfa) throws Exception {
		super();
		this.dfa = dfa;
		this.lexemeReader = lexemeReader;
	}
	
	
	private 

	
	public Token getNextToken() {
		Character currentChar;
		try {
			while ( ( currentChar = lexemeReader.getNextChar()) != SpecialChars.CHAR_EOF) {
		  	if ( dfa.canChangeStateByElement( currentChar)) {
		  		dfa.changeStateByElement( currentChar);
		  		if ( dfa.getCurrentState().isFiniteState()) {
		  			dfa.getCurrentState().getPayload();
		  		}
		  			
		  	}
		  	else 
		  		
		  	
		  }
		
		
		}
		stateMachine.canChangeStateBySymbol( symbol)
		return ;
	}
	

	

}
