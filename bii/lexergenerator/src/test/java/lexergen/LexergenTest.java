package lexergen;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import parser.IToken;

import tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;

public class LexergenTest {
	
	/**
	 * Test of getNextToken method, of class Lexergen.
	 */
	@Test
	public void testGetNextToken() throws Exception {
		File rdFile = new File("src/test/resources/def/lexergen/test.rd");
		File sourceFile = new File("src/test/resources/source/lexergen/test.fun");
		
		Lexergenerator lexergen = new Lexergen(rdFile, sourceFile, BuilderType.indirectBuilder, CorrectionMode.PANIC_MODE, true);		
		IToken currentToken = null;
		
		currentToken = lexergen.getNextToken();
		Assert.assertEquals("KEYWORD", currentToken.getType());
		Assert.assertEquals("IF", currentToken.getAttribute().toString());
	}
}
