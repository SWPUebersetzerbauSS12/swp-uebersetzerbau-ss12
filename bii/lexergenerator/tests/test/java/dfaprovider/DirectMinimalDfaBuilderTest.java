package dfaprovider;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import bufferedreader.*;

import regextodfaconverter.MinimalDfa;
import tokenmatcher.StatePayload;
import tokenmatcher.Token;
import tokenmatcher.Tokenizer;

/**
 * Test-Klasse für die DirectMinimalDfaBuilder-Klasse.
 * 
 * @author Daniel Rotar
 * 
 */
public class DirectMinimalDfaBuilderTest {

	/**
	 * Test of buildMinimalDfa method, of class IndirectMinimalDfaBuilder.
	 */
	@Test
	public void testBuildMinimalDfa() throws Exception {
		File rdFile = new File("src/test/resources/def/dfaprovider/test.rd");
		String sourceFilename = "src/test/resources/source/dfaprovider/test.fun";

		MinimalDfa<Character, StatePayload> mDfa = null;
		MinimalDfaBuilder builder = new DirectMinimalDfaBuilder();

//		mDfa = MinimalDfaProvider.getMinimalDfa(rdFile, builder);
		mDfa = builder.buildMinimalDfa(rdFile);
		
		LexemeReader lexemeReader = new BufferedLexemeReader(sourceFilename);
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
