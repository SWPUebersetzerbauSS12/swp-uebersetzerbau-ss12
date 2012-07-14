package de.fuberlin.projecta;

import static org.junit.Assert.fail;

import org.junit.Test;

import de.fuberlin.projecta.analysis.SemanticAnalyzer;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.io.StringCharStream;
import de.fuberlin.projecta.parser.ParseException;
import de.fuberlin.projecta.parser.Parser;

public class SemanticAnalysisTest {

	static String MAIN_DEF = "def int main() {return 0; }";

	@Test(expected = IllegalStateException.class)
	public void testInvalidCode() {
		final String code = "def int foo() { int a; int a; }";
		analyze(code);
	}

	@Test
	public void testValidFunctionDef() {
		final String code = MAIN_DEF + "def void foo(int a){int b;} def void foo(real a){int b;}";
		analyze(code);
	}

	@Test(expected = IllegalStateException.class)
	public void testInvalidFunctionDef() {
		final String code = MAIN_DEF + "def int foo() {} def real foo() {}";
		analyze(code);
	}

	@Test
	public void testDeclarationScope() {
		final String code = MAIN_DEF + "def int foo() { int a; { int a; } }";
		analyze(code);
	}

	@Test(expected = ClassCastException.class)
	public void testIncompatibleOperands() {
		final String code = MAIN_DEF + "def int foo() { int a; a = 0.0; }";
		analyze(code);
	}

	@Test
	public void testRecordAsReturnType(){
		final String code = MAIN_DEF + "def record {int real; int imag;} foo(){record {int real; int imag;} myRecord; return myRecord;}";
		analyze(code);
	}

	@Test
	public void testRecordBehaviour(){
		final String code = MAIN_DEF + "def int foobar(record {int r; int i;} myImaginaire){myImaginaire.r = 1; myImaginaire.i = 0;}";
		analyze(code);
	}

	@Test(expected = SemanticException.class)
	public void testMissingMain(){
		final String code = "def int foobar(){return 0;}";
		analyze(code);
	}

	private static void analyze(String code) {
		Lexer lexer = new Lexer(new StringCharStream(code));
		Parser parser = new Parser();
		try {
			parser.parse(lexer, "");
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
		SemanticAnalyzer analyzer = new SemanticAnalyzer(parser.getParseTree());
		analyzer.analyze();
		analyzer.getAST().checkSemantics();
	}
}
