package regextodfaconverter.directconverter;

import org.junit.Test;

import bufferedreader.BufferedLexemeReader;
import bufferedreader.LexemeReader;

import regextodfaconverter.MinimalDfa;
import regextodfaconverter.fsm.FiniteStateMachine;
import tokenmatcher.StatePayload;
import tokenmatcher.Token;
import tokenmatcher.Tokenizer;


public class RegexToDfaTest {

	@Test
	public void testReduceAndBracketRegex() throws Exception {

		FiniteStateMachine<Character, ? extends StatePayload> fsm = new RegexToDfaConverter()
				.convert( "(a|b)*abb", new regextodfaconverter.fsm.StatePayload( "OP", "LE"));

		LexemeReader lexemeReader = new BufferedLexemeReader( "src/test/resources/source/tokenmatcher.testrelop.fun");// new
																																						// SimpleLexemeReader(
		System.out.println(fsm);
				System.out.println();
		assert fsm.isDeterministic();
		System.out.println( fsm.isDeterministic());
		
		System.out.println(new MinimalDfa( fsm));
		Tokenizer tokenizer = new Tokenizer( lexemeReader, new MinimalDfa( fsm));
/*
		try {
		Token currentToken;
		while ( true) {
			currentToken = tokenizer.getNextToken();
			System.out.print( currentToken.getType());
			System.out.println( " " + currentToken.getAttribute());
		}
		} catch( EndOfFileException e){
			
		}
		*/
	}

}
