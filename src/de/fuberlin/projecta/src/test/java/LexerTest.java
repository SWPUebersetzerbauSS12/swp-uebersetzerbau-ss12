import lexer.IToken.TokenType;
import lexer.Lexer;
import lexer.SyntaxErrorException;
import lexer.Token;
import lexer.io.FileCharStream;
import lexer.io.StringCharStream;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class LexerTest {

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
				TokenType.OP_SEMIC);
		assertEquals(tokenList.get(tokenList.size() - 1).getType(),
				TokenType.EOF);
	}

	@Test
	public void testFunctionDeclaration() {
		final String code = "def int function();";

		Token[] expected = new Token[]{
				new Token(TokenType.DEF, null, 1, 0),
				new Token(TokenType.INT, null, 1, 4),
				new Token(TokenType.ID, "function", 1, 8),
				new Token(TokenType.LPAREN, null, 1, 16),
				new Token(TokenType.RPAREN, null, 1, 17),
				new Token(TokenType.OP_SEMIC, null, 1, 18),
				new Token(TokenType.EOF, null, 1, 19)
		};
		assertArrayEquals(expected, tokenize(code).toArray());
	}

	@Test(expected = SyntaxErrorException.class)
	public void testInvalidSentence() {
		String code = "a:";
		tokenize(code);
	}

	@Test
	public void testCharacterPositions() {
		final String code = "def int foo();\ndef int bar();";

		Token[] expected = new Token[]{
				new Token(TokenType.DEF, null, 1, 0),
				new Token(TokenType.INT, null, 1, 4),
				new Token(TokenType.ID, "foo", 1, 8),
				new Token(TokenType.LPAREN, null, 1, 11),
				new Token(TokenType.RPAREN, null, 1, 12),
				new Token(TokenType.OP_SEMIC, null, 1, 13),
				new Token(TokenType.DEF, null, 2, 0),
				new Token(TokenType.INT, null, 2, 4),
				new Token(TokenType.ID, "bar", 2, 8),
				new Token(TokenType.LPAREN, null, 2, 11),
				new Token(TokenType.RPAREN, null, 2, 12),
				new Token(TokenType.OP_SEMIC, null, 2, 13),
				new Token(TokenType.EOF, null, 2, 14)
		};
		assertArrayEquals(expected, tokenize(code).toArray());
	}

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
}
