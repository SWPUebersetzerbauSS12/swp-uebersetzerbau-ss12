import lexer.Lexer;
import lexer.io.StringCharStream;

import org.junit.Test;

import parser.Parser;
import parser.ParserException;
import semantic.analysis.SemanticAnalyzer;


public class SemanticAnalysisTest {

	@Test
	public void testInvalidCode() {
		final String code = "def int foo() { int a; int a; }";
		
		Lexer lexer = new Lexer(new StringCharStream(code));
		Parser parser = new Parser(lexer);
		
		try {
			parser.parse();
		} catch (ParserException e) {
			e.printStackTrace();
		}
		SemanticAnalyzer analyzer = new SemanticAnalyzer(parser.getSyntaxTree());
		analyzer.run();
	}

}
