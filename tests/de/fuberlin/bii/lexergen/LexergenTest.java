package de.fuberlin.bii.lexergen;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;


import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.bii.tokenmatcher.LexemIdentificationException;
import de.fuberlin.bii.tokenmatcher.Token;
import de.fuberlin.bii.tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;
import de.fuberlin.bii.utils.Notification;
import de.fuberlin.commons.lexer.ILexer;

public class LexergenTest {
	
	/**
	 * Test of getNextToken method, of class Lexergen.
	 */
	@Test
	public void testGetNextToken() throws Exception {
    Notification.enableDebugInfoPrinting();
    Notification.enableDebugPrinting();
		
		File rdFile = new File("tests/resources/de/fuberlin/bii/def/lexergen/test.rd");
		File sourceFile = new File("tests/resources/de/fuberlin/bii/source/lexergen/test.fun");
		

		//ILexer lexergen = new Lexergen(rdFile, sourceFile, BuilderType.indirectBuilder, CorrectionMode.PANIC_MODE, true);		
		ILexer lexergen = new Lexergen(rdFile, sourceFile, BuilderType.directBuilder, CorrectionMode.PANIC_MODE, true);		

		IToken currentToken = null;
		String tokenString;
		String[] tokensToFind = {""}; //TODO: ...
		int i = 0;

		do {
			try {
				currentToken = lexergen.getNextToken();
				tokenString = "<" + currentToken.getType() + ", " + currentToken.getAttribute().toString() + ">";
				//Assert.assertEquals(tokensToFind[i], tokenString);
				System.out.println(tokenString);
				i++;
			} catch (RuntimeException e) {
				Notification.printDebugMessage( e.getMessage());
			}
		} while( !Token.isEofToken( currentToken));

		
		//Assert.assertEquals(i, tokensToFind.length);
	}
}
