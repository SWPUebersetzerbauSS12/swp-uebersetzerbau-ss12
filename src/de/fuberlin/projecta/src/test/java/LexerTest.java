import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import lexer.IToken.TokenType;
import lexer.Lexer;
import lexer.SyntaxErrorException;
import lexer.Token;
import lexer.io.FileCharStream;
import lexer.io.StringCharStream;

import org.junit.Test;

public class LexerTest {

	private ArrayList<Token> tokenize(String data) {
		Lexer lexer = new Lexer(new StringCharStream(data));
		return tokenize(lexer);
	}

	private ArrayList<Token> tokenize(Lexer lexer) {
		ArrayList<Token> tokenList = new ArrayList<Token>();

		Token t;
		do {
			t = lexer.getNextToken();
			tokenList.add(t);
		} while (t.getType() != TokenType.EOF);
		return tokenList;
	}

	@Test
	public void testReadSourceFile() throws SyntaxErrorException {
		final String path = Config.TEST_DATA_FOLDER + "LexerTestFile1.txt";

		File sourceFile = new File(path);
		assertTrue(sourceFile.exists());
		assertTrue(sourceFile.canRead());

		Lexer lexer = new Lexer(new FileCharStream(path));
		ArrayList<Token> tokenList = tokenize(lexer);
		assertEquals(tokenList.size(), 12);
		assertEquals(tokenList.get(0).getType(), TokenType.DEF);
		assertEquals(tokenList.get(tokenList.size() - 2).getType(),
				TokenType.SEMIC);
		assertEquals(tokenList.get(tokenList.size() - 1).getType(),
				TokenType.EOF);
	}

	@Test
	public void testFunctionDeclaration() {
		final String code = "def int function();";
		ArrayList<Token> tokenList = tokenize(code);

		int index = -1;
		assertEquals(tokenList.size(), 7);
		assertEquals(tokenList.get(++index).getType(), TokenType.DEF);
		assertEquals(tokenList.get(++index).getType(), TokenType.INT);

		assertEquals(tokenList.get(++index).getType(), TokenType.ID);
		assertEquals(tokenList.get(index).getAttribute(), "function");

		assertEquals(tokenList.get(++index).getType(), TokenType.BRL);
		assertEquals(tokenList.get(++index).getType(), TokenType.BRR);
		assertEquals(tokenList.get(++index).getType(), TokenType.SEMIC);
		assertEquals(tokenList.get(++index).getType(), TokenType.EOF);
	}

	@Test(expected = SyntaxErrorException.class)
	public void testInvalidSentence() {
		String code = "a:";
		tokenize(code);
	}

	@Test
	public void testCharacterPositions() {
		final String code = "def int foo();\ndef int bar();";

		ArrayList<Token> tokenList = tokenize(code);
		assertEquals(tokenList.get(0).getType(), TokenType.DEF);
		assertEquals(tokenList.get(0).getLineNumber(), 1);
		assertEquals(tokenList.get(0).getOffset(), 0);
		assertEquals(tokenList.get(1).getLineNumber(), 1);
		assertEquals(tokenList.get(1).getOffset(), 4);

		final int index = 8; // expected token index for bar
		assertEquals(tokenList.get(index).getType(), TokenType.ID);
		assertEquals(tokenList.get(index).getAttribute(), "bar");
		assertEquals(tokenList.get(index).getLineNumber(), 2);
		assertEquals(tokenList.get(index).getOffset(), 9);
	}

}
