import static org.junit.Assert.assertTrue;
import lexer.Lexer;
import lexer.io.StringCharStream;

import org.junit.Test;

import parser.ParseException;
import parser.Parser;
import analysis.SemanticAnalyzer;

public class SemanticAnalysisTest {

	public static void analyze(String code) {
		Lexer lexer = new Lexer(new StringCharStream(code));
		Parser parser = new Parser();
		try {
			parser.parse(lexer, "");
		} catch (ParseException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		SemanticAnalyzer analyzer = new SemanticAnalyzer(parser.getParseTree());
		analyzer.analyze();
	}

	@Test(expected = IllegalStateException.class)
	public void testInvalidCode() {
		final String code = "def int foo() { int a; int a; }";
		analyze(code);
	}
	
	@Test
	public void testValidFunctionDef() {
		final String code = "def void foo(int a){int b;} def void foo(real a){int b;}";
		analyze(code);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testInvalidFunctionDef() {
		final String code = "def int foo() {} def real foo() {}";
		analyze(code);
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
	
	@Test
	public void testRecordAsReturnType(){
		final String code = "def record {int real; int imag;} foo(){record {int real; int imag;} myRecord; return myRecord;}";
		analyze(code);
	}
	
	@Test
	public void testRecordBehaviour(){
		final String code = "def int foobar(record {int r; int i;} myImaginaire){myImaginaire.r = 1; myImaginaire.i = 0;}";
		analyze(code);
	}

}
