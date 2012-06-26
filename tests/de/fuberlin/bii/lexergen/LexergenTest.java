package de.fuberlin.bii.lexergen;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;


import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.bii.tokenmatcher.Token;
import de.fuberlin.bii.tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;
import de.fuberlin.commons.lexer.ILexer;

public class LexergenTest {
	
	/**
	 * Test of getNextToken method, of class Lexergen.
	 */
	@Test
	public void testGetNextToken() throws Exception {
		File rdFile = new File("tests/resources/de/fuberlin/bii/def/lexergen/test.rd");
		File sourceFile = new File("tests/resources/de/fuberlin/bii/source/lexergen/test.fun");
		

		ILexer lexergen = new Lexergen(rdFile, sourceFile, BuilderType.indirectBuilder, CorrectionMode.PANIC_MODE, true);		
		//ILexer lexergen = new Lexergen(rdFile, sourceFile, BuilderType.directBuilder, CorrectionMode.PANIC_MODE, true);		

		IToken currentToken;
		String tokenString;
		String[] tokensToFind = {""}; //TODO: ...
		int i = 0;
		
		while ( !Token.isEofToken( currentToken = lexergen.getNextToken())) {
			tokenString = "<" + currentToken.getType() + ", " + currentToken.getAttribute().toString() + ">";
			//Assert.assertEquals(tokensToFind[i], tokenString);
			System.out.println(tokenString);
			i++;
		}
		
		//Assert.assertEquals(i, tokensToFind.length);
	}
}
