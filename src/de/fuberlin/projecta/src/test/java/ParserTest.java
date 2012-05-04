import static org.junit.Assert.assertTrue;

import java.io.File;

import lexer.Lexer;
import lexer.SyntaxErrorException;
import lexer.io.FileCharStream;

import org.junit.Test;

import parser.Parser;
import parser.ParserException;


public class ParserTest {

	@Test
	public void testReadSourceFile() throws SyntaxErrorException {
		final String path = Config.TEST_DATA_FOLDER + "LexerTestFile2.txt";
		
		File sourceFile = new File(path);
		assertTrue(sourceFile.exists());
		assertTrue(sourceFile.canRead());

		Lexer lexer = new Lexer(new FileCharStream(path));
		Parser parser = new Parser(lexer);
		try {
			parser.parse();
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
}
