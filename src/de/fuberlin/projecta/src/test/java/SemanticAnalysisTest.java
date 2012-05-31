import static org.junit.Assert.assertTrue;
import lexer.Lexer;
import lexer.io.StringCharStream;

import org.junit.Test;

import analysis.SemanticAnalyzer;
import analysis.SemanticException;

import parser.Parser;
import parser.ParseException;

public class SemanticAnalysisTest {

	public static void analyze(String code) {
		Lexer lexer = new Lexer(new StringCharStream(code));
		Parser parser = new Parser(lexer);
		try {
			parser.parse();
		} catch (ParseException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		SemanticAnalyzer analyzer = new SemanticAnalyzer(parser.getParseTree());
		analyzer.analyze();
	}

	@Test
	public void testInvalidCode() {
		final String code = "def int foo() { int a; int a; }";
		try {
			analyze(code);
		} catch (SemanticException e) {
			System.out.println("Semantic analysis failed.");
			System.out.println(e.toString());
		}
	}

	@Test
	public void testDeclarationScope() {
		final String code = "def int foo() { int a; { int a; } }";
		analyze(code);
	}

	@Test(expected = ClassCastException.class)
	public void testIncompatibleOperands() {
		final String code = "def int foo() { int a; a = 0.0; }";
		analyze(code);
	}

}
