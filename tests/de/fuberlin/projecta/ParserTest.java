package de.fuberlin.projecta;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.SyntaxErrorException;
import de.fuberlin.projecta.lexer.io.FileCharStream;
import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.lexer.io.StringCharStream;
import de.fuberlin.projecta.parser.ParseException;
import de.fuberlin.projecta.parser.Parser;

public class ParserTest {

	static String mainC(String block) {
		String code = "def int main() { ";
		code += block;
		code += "return 0; }";
		return code;
	}

	void parse(ICharStream stream) {
		ILexer lexer = new Lexer(stream);
		Parser parser = new Parser();
		parser.parse(lexer, "");
	}

	void parse(String code) {
		ICharStream stream = new StringCharStream(code);
		parse(stream);
	}

	@Test
	public void testReadSourceFile() {
		final String path = Config.TEST_DATA_FOLDER + "LexerTestFile2.txt";
		
		File sourceFile = new File(path);
		assertTrue(sourceFile.exists());
		assertTrue(sourceFile.canRead());
		parse(new FileCharStream(path));
	}

	@Test(expected=ParseException.class)
	public void testInvalidProgram() {
		parse("def def");
	}

	@Test(expected=SyntaxErrorException.class)
	public void testReceiveLexerException() {
		parse("0e.");
	}

	@Test
	public void testValidProgram() {
		parse("def int main() {}");
	}
}
