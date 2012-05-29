import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import lexer.Lexer;
import lexer.SyntaxErrorException;
import lexer.Token;
import lexer.TokenType;
import lexer.io.FileCharStream;
import lexer.io.StringCharStream;

import org.junit.Test;

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
		assertEquals(tokenList.get(0).getInternalType(), TokenType.DEF);
		assertEquals(tokenList.get(tokenList.size() - 2).getInternalType(),
				TokenType.OP_SEMIC);
		assertEquals(tokenList.get(tokenList.size() - 1).getInternalType(),
				TokenType.EOF);
	}
	
	
	@Test
	public void testNumericalLiterals() throws SyntaxErrorException {
		String source = "23 \n3.14+ \n2. \n22e+4 \n31.4e-1";

		ArrayList<Token> tokenList = tokenize(source);

		assertEquals(tokenList.get(0).getInternalType(), TokenType.INT_LITERAL);
		assertEquals((Integer) (tokenList.get(0).getAttribute()), (Integer) 23);

		assertEquals(tokenList.get(1).getInternalType(), TokenType.REAL_LITERAL);
		assertEquals((Float) (tokenList.get(1).getAttribute()), (Float) 3.14f);
		assertEquals(tokenList.get(2).getInternalType(), TokenType.OP_ADD);
		assertEquals(tokenList.get(3).getInternalType(), TokenType.REAL_LITERAL);
		assertEquals((Float) (tokenList.get(3).getAttribute()), (Float) 2.f);

		assertEquals(tokenList.get(4).getInternalType(), TokenType.REAL_LITERAL);
		assertEquals((Float) (tokenList.get(4).getAttribute()), (Float) 22e4f);

		assertEquals(tokenList.get(5).getInternalType(), TokenType.REAL_LITERAL);
		assertEquals((Float) (tokenList.get(5).getAttribute()),
				(Float) 31.4e-1f);
	}

	@Test
	public void testBooleanLiterals() {
		final String source = "true; false;";

		ArrayList<Token> tokenList = tokenize(source);

		assertEquals(tokenList.get(0).getInternalType(), TokenType.BOOL_LITERAL);
		assertEquals((Boolean) (tokenList.get(0).getAttribute()),
				(Boolean) true);

		assertEquals(tokenList.get(2).getInternalType(), TokenType.BOOL_LITERAL);
		assertEquals((Boolean) (tokenList.get(2).getAttribute()),
				(Boolean) false);
	}

	@Test
	public void testCommentary() {
		final String code = "def int function();\n/*this should be \nignored*/ def real func();";

		Token[] expected = new Token[] { new Token(TokenType.DEF, null, 1, 0),
				new Token(TokenType.INT_TYPE, null, 1, 4),
				new Token(TokenType.ID, "function", 1, 8),
				new Token(TokenType.LPAREN, null, 1, 16),
				new Token(TokenType.RPAREN, null, 1, 17),
				new Token(TokenType.OP_SEMIC, null, 1, 18),
				new Token(TokenType.DEF, null, 3, 11),
				new Token(TokenType.REAL_TYPE, null, 3, 15),
				new Token(TokenType.ID, "func", 3, 20),
				new Token(TokenType.LPAREN, null, 3, 24),
				new Token(TokenType.RPAREN, null, 3, 25),
				new Token(TokenType.OP_SEMIC, null, 3, 26),
				new Token(TokenType.EOF, null, 3, 27) };
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
	
	@Test
	public void testArrayDeclaration() {
		
//		<DEF, null, 1, 0>
//		<INT_TYPE, null, 1, 4>
//		<ID, foobar, 1, 8>
//		<LPAREN, null, 1, 14>
//		<RPAREN, null, 1, 15>
//		<LBRACE, null, 1, 16>
//		<INT_TYPE, null, 1, 17>
//		<LBRACKET, null, 1, 21>
//		<INT_LITERAL, 3, 1, 23>
//		<RBRACKET, null, 1, 23>
//		<ID, myArray, 1, 25>
//		<OP_SEMIC, null, 1, 32>
//		<RBRACE, null, 1, 33>
//		<EOF, null, 2, 0>
		
		final String code = "def int foobar(){int[3] myArray;}";

		Token[] expected = new Token[] { new Token(TokenType.DEF, null, 1, 0),
				new Token(TokenType.INT_TYPE, null, 1, 4),
				new Token(TokenType.ID, "foobar", 1, 8),
				new Token(TokenType.LPAREN, null, 1, 14),
				new Token(TokenType.RPAREN, null, 1, 15),
				new Token(TokenType.LBRACE, null, 1, 16),
				new Token(TokenType.INT_TYPE, null,1,17),
				new Token(TokenType.LBRACKET, null, 1, 20),
				new Token(TokenType.INT_LITERAL, 3, 1, 22),
				new Token(TokenType.RBRACKET, null, 1, 22),
				new Token(TokenType.ID, "myArray", 1, 24),
				new Token(TokenType.OP_SEMIC, null, 1, 31),
				new Token(TokenType.RBRACE, null, 1, 32),
				new Token(TokenType.EOF, null, 1, 33) };
		assertArrayEquals(expected, tokenize(code).toArray());
	}

	@Test(expected = SyntaxErrorException.class)
	public void testInvalidSentence() {
		String code = "a:";
		tokenize(code);
	}

	@Test
	public void testBasicTypes() {
		final String code = "bool b; int i; real r; string s;";

		ArrayList<Token> tokenList = tokenize(code);
		assertEquals(tokenList.get(0).getInternalType(), TokenType.BOOL_TYPE);
		assertEquals(tokenList.get(3).getInternalType(), TokenType.INT_TYPE);
		assertEquals(tokenList.get(6).getInternalType(), TokenType.REAL_TYPE);
		assertEquals(tokenList.get(9).getInternalType(), TokenType.STRING_TYPE);
	}

	@Test
	public void testNoSpacesBetweenTokens() {
		{
			final String code = "record{int i;}";
			ArrayList<Token> tokenList = tokenize(code);
			assertEquals(tokenList.get(0).getInternalType(), TokenType.RECORD);
			assertEquals(tokenList.get(1).getInternalType(), TokenType.LBRACE);
			assertEquals(tokenList.get(2).getInternalType(), TokenType.INT_TYPE);
		}

		{
			final String code = "a[1];";
			ArrayList<Token> tokenList = tokenize(code);
			assertEquals(tokenList.get(0).getInternalType(), TokenType.ID);
			assertEquals(tokenList.get(1).getInternalType(), TokenType.LBRACKET);
			assertEquals(tokenList.get(2).getInternalType(), TokenType.INT_LITERAL);
		}

		{
			final String code = "def int foo(int i);";
			ArrayList<Token> tokenList = tokenize(code);
			assertEquals(tokenList.get(2).getInternalType(), TokenType.ID);
			assertEquals(tokenList.get(3).getInternalType(), TokenType.LPAREN);
		}
	}

	@Test
	public void testBooleanType() {
		final String code = "def int foobar(bool i);";
		Token[] expected = new Token[] { new Token(TokenType.DEF, null, 1, 0),
				new Token(TokenType.INT_TYPE, null, 1, 4),
				new Token(TokenType.ID, "foobar", 1, 8),
				new Token(TokenType.LPAREN, null, 1, 14),
				new Token(TokenType.BOOL_TYPE, null, 1, 15),
				new Token(TokenType.ID, "i", 1, 20),
				new Token(TokenType.RPAREN, null, 1, 21),
				new Token(TokenType.OP_SEMIC, null, 1, 22),
				new Token(TokenType.EOF, null, 1, 23) };
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
				new Token(TokenType.EOF, null, 1, 28) };
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
		} while (!t.getType().equals("EOF"));
		return tokenList;
	}

}
