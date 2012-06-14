package de.fuberlin.bii.lexergen;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import de.fuberlin.bii.tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;
import de.fuberlin.commons.lexer.IToken;

public class LexergenTest {
	
	/**
	 * Test of getNextToken method, of class Lexergen.
	 */
	@Test
	public void testGetNextToken() throws Exception {
		File rdFile = new File("tests/resources/de/fuberlin/bii/def/lexergen/test.rd");
		File sourceFile = new File("tests/resources/de/fuberlin/bii/source/lexergen/test.fun");
		
	    Lexergenerator lexergen = new Lexergen(rdFile, sourceFile, BuilderType.indirectBuilder, CorrectionMode.PANIC_MODE, true);		
//		Lexergenerator lexergen = new Lexergen(rdFile, sourceFile, BuilderType.directBuilder, CorrectionMode.PANIC_MODE, true);		
		IToken currentToken = null;
		
		currentToken = lexergen.getNextToken();
		Assert.assertEquals("KEYWORD", currentToken.getType());
		Assert.assertEquals("IF", currentToken.getAttribute().toString());
		//TODO: Test zu Ende schreiben...
	}
}
