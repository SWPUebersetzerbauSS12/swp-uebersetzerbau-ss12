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
	public void testNumericalValues() throws SyntaxErrorException {
		// 23
		// 3.14
		// 2.
		// 22e+4
		// 31.4e-1
		String source = "23 \n3.14+ \n2. \n22e+4 \n31.4e-1";

		Lexer lexer = new Lexer(new StringCharStream(source));
		ArrayList<Token> tokenList = tokenize(lexer);

		assertEquals(tokenList.get(0).getType(), TokenType.INT_LITERAL);
		assertEquals(tokenList.get(1).getType(), TokenType.REAL_LITERAL);
		assertEquals(tokenList.get(2).getType(), TokenType.OP_ADD);
		assertEquals(tokenList.get(3).getType(), TokenType.REAL_LITERAL);
		assertEquals(tokenList.get(4).getType(), TokenType.REAL_LITERAL);
		assertEquals(tokenList.get(5).getType(), TokenType.REAL_LITERAL);
	}
	
	@Test
	public void testCommentary(){
		final String code = "def int function();\n/*this should be \nignored*/ def real func();";
		
		Token[] expected = new Token[]{
				new Token(TokenType.DEF, null, 1,0),
				new Token(TokenType.INT_TYPE, null, 1, 4),
				new Token(TokenType.ID, "function", 1, 8),
				new Token(TokenType.LPAREN, null, 1, 16),
				new Token(TokenType.RPAREN, null, 1, 17),
				new Token(TokenType.OP_SEMIC, null, 1, 18),
				new Token(TokenType.DEF, null, 3,11),
				new Token(TokenType.REAL_TYPE, null, 3, 15),
				new Token(TokenType.ID, "func", 3, 20),
				new Token(TokenType.LPAREN, null, 3, 24),
				new Token(TokenType.RPAREN, null, 3, 25),
				new Token(TokenType.OP_SEMIC, null, 3, 26),
				new Token(TokenType.EOF, null, 3, 27)
		};
		assertArrayEquals(expected, tokenize(code).toArray());
	}

	@Test
	public void testFunctionDeclaration() {
		final String code = "def int function();";

		Token[] expected = new Token[] { new Token(TokenType.DEF, null, 1, 0),
				new Token(TokenType.INT_TYPE, null, 1, 4),
				new Token(TokenType.ID, "function", 1, 8),
				new Token(TokenType.LPAREN, null, 1, 16),
				new Token(TokenType.RPAREN, null, 1, 17),
				new Token(TokenType.OP_SEMIC, null, 1, 18),
				new Token(TokenType.EOF, null, 1, 19) };
		assertArrayEquals(expected, tokenize(code).toArray());
	}

	@Test(expected = SyntaxErrorException.class)
	public void testInvalidSentence() {
		String code = "a:";
		tokenize(code);
	}

	@Test
	public void testBooleanLiterals() {
		final String code = "return true;\nreturn false;";
		Token[] expected = new Token[] {
				new Token(TokenType.RETURN, null, 1, 0),
				new Token(TokenType.BOOL_LITERAL, "true", 1, 7),
				new Token(TokenType.OP_SEMIC, null, 1, 11),

				new Token(TokenType.RETURN, null, 2, 0),
				new Token(TokenType.BOOL_LITERAL, "false", 2, 7),
				new Token(TokenType.OP_SEMIC, null, 2, 12),
				new Token(TokenType.EOF, null, 2, 13)
		};
		assertArrayEquals(expected, tokenize(code).toArray());
	}
	
	@Test
	public void testBooleanType() {
		final String code = "def int foobar (bool i);";
		Token[] expected = new Token[] {
				new Token(TokenType.DEF, null, 1,0),
				new Token(TokenType.INT_TYPE, null, 1, 4),
				new Token(TokenType.ID, "foobar", 1, 8),
				new Token(TokenType.LPAREN, null, 1, 14),
				new Token(TokenType.BOOL_TYPE, null, 1, 15),
				new Token(TokenType.ID, "i", 1, 20),
				new Token(TokenType.RPAREN, null, 1, 21),
				new Token(TokenType.OP_SEMIC, null, 1, 22),
				new Token(TokenType.EOF, null, 2, 0)
		};
		assertArrayEquals(expected, tokenize(code).toArray());
	}

	@Test
	public void testRecordKeyword() {
		final String code = "record { int a; real b; } r;";
		Token[] expected = new Token[] {
				new Token(TokenType.RECORD, null, 1, 0),
				new Token(TokenType.LBRACE, null, 1, 7),
				new Token(TokenType.INT_TYPE, null, 1, 9),
				new Token(TokenType.ID, "a", 1, 13),
				new Token(TokenType.OP_SEMIC, null, 1, 14),
				new Token(TokenType.REAL_TYPE, null, 1, 16),
				new Token(TokenType.ID, "b", 1, 21),
				new Token(TokenType.OP_SEMIC, null, 1, 22),
				new Token(TokenType.RBRACE, null, 1, 24),
				new Token(TokenType.ID, "r", 1, 26),
				new Token(TokenType.OP_SEMIC, null, 1, 27),
				new Token(TokenType.EOF, null, 1, 28)
		};
		assertArrayEquals(expected, tokenize(code).toArray());
	}

	@Test
	public void testCharacterPositions() {
		final String code = "def int foo();\ndef int bar();";

		Token[] expected = new Token[] { new Token(TokenType.DEF, null, 1, 0),
				new Token(TokenType.INT_TYPE, null, 1, 4),
				new Token(TokenType.ID, "foo", 1, 8),
				new Token(TokenType.LPAREN, null, 1, 11),
				new Token(TokenType.RPAREN, null, 1, 12),
				new Token(TokenType.OP_SEMIC, null, 1, 13),
				new Token(TokenType.DEF, null, 2, 0),
				new Token(TokenType.INT_TYPE, null, 2, 4),
				new Token(TokenType.ID, "bar", 2, 8),
				new Token(TokenType.LPAREN, null, 2, 11),
				new Token(TokenType.RPAREN, null, 2, 12),
				new Token(TokenType.OP_SEMIC, null, 2, 13),
				new Token(TokenType.EOF, null, 2, 14) };
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
