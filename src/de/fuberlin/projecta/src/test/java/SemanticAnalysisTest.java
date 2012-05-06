import lexer.Lexer;
import lexer.io.StringCharStream;

import org.junit.Test;

import parser.Parser;
import parser.ParserException;
import semantic.analysis.SemanticAnalyser;


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
		SemanticAnalyser analyzer = new SemanticAnalyser(parser.getSyntaxTree());
		analyzer.run();
	}

}
