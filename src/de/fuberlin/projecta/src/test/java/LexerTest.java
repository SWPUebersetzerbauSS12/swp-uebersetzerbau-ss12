import lexer.ILexer;
import lexer.Lexer;
import lexer.SyntaxErrorException;
import lexer.Token;
import org.junit.Test;

import java.io.File;

public class LexerTest {

	@Test
	public void test() throws SyntaxErrorException {
		final String path = Config.TEST_DATA_FOLDER + "LexerTestFile1.txt";

		File sourceFile = new File(path);
		assert (sourceFile.exists());
		assert (sourceFile.canRead());

		ILexer lex = new Lexer(path);
		Token t;
		while ((t = lex.getNextToken()) != null) {
			System.out.println(t);
		}
	}


}
