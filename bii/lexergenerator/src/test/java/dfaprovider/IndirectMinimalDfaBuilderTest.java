package dfaprovider;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import regextodfaconverter.MinimalDfa;
import tokenmatcher.StatePayload;
import tokenmatcher.Token;
import tokenmatcher.Tokenizer;
import bufferedreader.BufferedLexemeReader;
import bufferedreader.LexemeReader;

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
		String rdFile = "src/test/resources/def/dfaprovider/test.rd";
		String sourceFile = "src/test/resources/source/dfaprovider/test.fun";

		MinimalDfa<Character, StatePayload> mDfa = null;
		MinimalDfaBuilder builder = new IndirectMinimalDfaBuilder();

//		mDfa = MinimalDfaProvider.getMinimalDfa(new File(rdFile), builder);
		mDfa = builder.buildMinimalDfa(new File(rdFile));
		
		LexemeReader lexemeReader = new BufferedLexemeReader(new File(sourceFile));
//		LexemeReader lexemeReader = new SimpleLexemeReader(sourceFile);
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
