package dfaprovider;

import java.io.File;

import lexergen.Settings;

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
		String rdFile = "src/test/resources/def/dfaprovider.test.rd";
		String sourceFile = "src/test/resources/source/dfaprovider.test.fun";

		Settings.readSettings();

		MinimalDfa<Character, StatePayload> mDfa = null;
		MinimalDfaBuilder builder = new DirectMinimalDfaBuilder();

		mDfa = builder.buildMinimalDfa(new File(rdFile));

		LexemeReader lexemeReader = new BufferedLexemeReader(sourceFile);
//		LexemeReader lexemeReader = new SimpleLexemeReader(sourceFile);
		Tokenizer tokenizer = new Tokenizer(lexemeReader, mDfa);

		Token currentToken;
		String tokenString;
		String[] tokensToFind = {}; //TODO Daniel:...
		int i = 0;
		while (true) {
			currentToken = tokenizer.getNextToken();
			tokenString = "<" + currentToken.getType() + ", " + currentToken.getAttribute() + ">";
			Assert.assertEquals(tokensToFind[i], tokenString);
			System.out.println(tokenString);
			i++;
		}
	}
}
