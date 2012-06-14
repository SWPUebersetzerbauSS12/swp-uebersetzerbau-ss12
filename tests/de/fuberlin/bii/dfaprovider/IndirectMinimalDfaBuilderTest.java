package de.fuberlin.bii.dfaprovider;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import de.fuberlin.bii.bufferedreader.BufferedLexemeReader;
import de.fuberlin.bii.bufferedreader.LexemeReader;
import de.fuberlin.bii.regextodfaconverter.MinimalDfa;
import de.fuberlin.bii.tokenmatcher.StatePayload;
import de.fuberlin.bii.tokenmatcher.Token;
import de.fuberlin.bii.tokenmatcher.Tokenizer;

/**
 * Test-Klasse für die IndirectMinimalDfaBuilder-Klasse.
 * 
 * @author Daniel Rotar
 * 
 */
public class IndirectMinimalDfaBuilderTest {

	/**
	 * Test of buildMinimalDfa method, of class IndirectMinimalDfaBuilder.
	 */
	@Test
	public void testBuildMinimalDfa() throws Exception {
		File rdFile = new File("tests/resources/de/fuberlin/bii/def/dfaprovider/test.rd");
		String sourceFilename = "tests/resources/de/fuberlin/bii/source/dfaprovider/test.fun";

		MinimalDfa<Character, StatePayload> mDfa = null;
		MinimalDfaBuilder builder = new IndirectMinimalDfaBuilder();

		mDfa = builder.buildMinimalDfa(rdFile);
		
		LexemeReader lexemeReader = new BufferedLexemeReader(sourceFilename);
		Tokenizer tokenizer = new Tokenizer(lexemeReader, mDfa);

		Token currentToken;
		String tokenString;
		String[] tokensToFind = {"<KEYWORD, IF>", "<ID, myvar9>", "<BRACKET, {>", "<KEYWORD, RETURN>", "<ID, myvar9>", "<BRACKET, }>"};
		int i = 0;
		while ( !Token.isEofToken( currentToken = tokenizer.getNextToken())) {
			tokenString = "<" + currentToken.getType() + ", " + currentToken.getAttribute().toString() + ">";
			Assert.assertEquals(tokensToFind[i], tokenString);
			System.out.println(tokenString);
			i++;
		}
		
		Assert.assertEquals(i, tokensToFind.length);
	}
}
