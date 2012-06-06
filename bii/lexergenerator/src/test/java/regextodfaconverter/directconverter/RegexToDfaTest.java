package regextodfaconverter.directconverter;

import java.io.File;

import org.junit.Test;

import bufferedreader.BufferedLexemeReader;
import bufferedreader.LexemeReader;

import regextodfaconverter.MinimalDfa;
import regextodfaconverter.fsm.FiniteStateMachine;
import tokenmatcher.StatePayload;
import tokenmatcher.Token;
import tokenmatcher.Tokenizer;
import utils.Notification;
import tokenmatcher.attributes.StringAttribute;


public class RegexToDfaTest {

	@Test
	public void testReduceAndBracketRegex() throws Exception {

		
		Notification.enableDebugPrinting();
		
		FiniteStateMachine<Character, ? extends StatePayload> fsm = new RegexToDfaConverter()
		.convert( "a.a+a*", new regextodfaconverter.fsm.StatePayload( "OP", new StringAttribute( "LE")));
			//	.convert( "<=|<>|<<|<", new regextodfaconverter.fsm.StatePayload( "OP", new StringAttribute( "LE")));
			//	.convert( "(a|b)*abb", new regextodfaconverter.fsm.StatePayload( "OP", new StringAttribute( "LE")));


		LexemeReader lexemeReader = new BufferedLexemeReader("src/test/resources/source/tokenmatcher.testrelop.fun");// new SimpleLexemeReader(
		System.out.println(fsm);
				System.out.println();
		assert fsm.isDeterministic();
		System.out.println( fsm.isDeterministic());
		System.out.println(new MinimalDfa( fsm));
		Tokenizer tokenizer = new Tokenizer( lexemeReader, new MinimalDfa( fsm));

		
		Token currentToken = null;
		while ( utils.Test.isUnassigned( currentToken) 
				|| !currentToken.equals( Token.getEofToken())) {
			currentToken = tokenizer.getNextToken();
			System.out.print( currentToken.getType());
			System.out.println( " " + currentToken.getAttribute());
		}

		
	}

}
