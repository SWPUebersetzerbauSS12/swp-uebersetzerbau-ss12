package de.fuberlin.projecta;

import org.junit.Test;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SemanticAnalyzer;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.TypeErrorException;

public class SemanticAnalysisTest {

	// "forward declare"
	static String mainC(String block) {
		return ParserTest.mainC(block);
	}

	@Test(expected = IllegalStateException.class)
	public void testInvalidCode() {
		final String code = mainC("int a; int a;");
		analyze(code);
	}

	@Test
	public void testValidFunctionDef() {
		final String code = mainC("") + "def void foo(int a){int b;} def void foo(real a){int b;}";
		analyze(code);
	}

	@Test(expected = IllegalStateException.class)
	public void testInvalidFunctionDef() {
		final String code = mainC("") + "def int foo() {} def real foo() {}";
		analyze(code);
	}

	@Test
	public void testDeclarationScope() {
		final String code = mainC("int a; { int a; }");
		analyze(code);
	}

	@Test(expected = TypeErrorException.class)
	public void testIncompatibleOperands() {
		final String code = mainC("int a; a = 0.0;");
		analyze(code);
	}

	@Test
	public void testRecordAsReturnType(){
		final String code = mainC("") + "def record {int real; int imag;} foo(){record {int real; int imag;} myRecord; return myRecord;}";
		analyze(code);
	}

	@Test
	public void testMultiExpression() {
		String code = mainC("int a; a = 1 + 2 + 3;");
		analyze(code);
	}

	@Test
	public void testRecordBehaviour(){
		final String code = mainC("") + "def int foobar(record {int r; int i;} myImaginaire){myImaginaire.r = 1; myImaginaire.i = 0;}";
		analyze(code);
	}

	@Test(expected = SemanticException.class)
	public void testMissingMain(){
		final String code = "def int foobar(){return 0;}";
		analyze(code);
	}

	static void analyze(String code) {
		ISyntaxTree parseTree = ParserTest.parse(code);
		SemanticAnalyzer analyzer = new SemanticAnalyzer(parseTree);
		analyzer.analyze();
		// TODO: Call the next methods in analyer.analyze()?
		analyzer.getAST().checkSemantics();
		analyzer.getAST().checkTypes();
	}
}
