package de.fuberlin.projecta;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.SyntaxErrorException;
import de.fuberlin.projecta.lexer.io.FileCharStream;
import de.fuberlin.projecta.parser.ParseException;
import de.fuberlin.projecta.parser.Parser;


public class ParserTest {

	@Test
	public void testReadSourceFile() throws SyntaxErrorException {
		final String path = Config.TEST_DATA_FOLDER + "LexerTestFile2.txt";
		
		File sourceFile = new File(path);
		assertTrue(sourceFile.exists());
		assertTrue(sourceFile.canRead());

		Lexer lexer = new Lexer(new FileCharStream(path));
		Parser parser = new Parser();
		try {
			parser.parse(lexer, "");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		parser.printParseTree();
	}
}
