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
				.convert( "<=|<>", new regextodfaconverter.fsm.StatePayload( "OP", "LE"));

		LexemeReader lexemeReader = new BufferedLexemeReader( "src/test/resources/source/tokenmatcher.testrelop.fun");// new
																																						// SimpleLexemeReader(
																																						// "testrelop.fun");
		Tokenizer tokenizer = new Tokenizer( lexemeReader, new MinimalDfa( fsm));

		Token currentToken;
		while ( true) {
			currentToken = tokenizer.getNextToken();
			System.out.print( currentToken.getType());
			System.out.println( " " + currentToken.getAttribute());
		}
	}

}
