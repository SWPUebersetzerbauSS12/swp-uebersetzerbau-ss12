package tokenmatcher;

import tokenmatcher.fsm.FiniteStateMachine;
import tokenmatcher.fsm.Symbol;
import tokenmatcher.fsm.TransitionTable;
import bufferedreader.LexemeReader;
import bufferedreader.SpecialChars;


public class Tokenizer implements LexerToParserInterface {
	
	private FiniteStateMachine<Character> stateMachine;
	
	private LexemeReader lexemeReader;
	
	public Tokenizer( LexemeReader lexemeReader, TransitionTable<Character> transitionTable) throws Exception {
		super();
		stateMachine = new FiniteStateMachine<Character>( transitionTable);
		this.lexemeReader = lexemeReader;
	}
	
	
	private 

	
	public Token getNextToken() {
		Character currentChar;
		try {
			while ( ( currentChar = lexemeReader.getNextChar()) != SpecialChars.CHAR_EOF) {
		  	if ( stateMachine.canChangeStateByElement( currentChar)) {
		  		stateMachine.changeStateByElement( currentChar);
		  		stateMachine.getCurrentState().i
		  	}
		  	else 
		  		
		  	
		  }
		
		
		}
		stateMachine.canChangeStateBySymbol( symbol)
		return ;
	}
	

	

}
