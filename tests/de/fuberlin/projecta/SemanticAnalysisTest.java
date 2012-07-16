package de.fuberlin.projecta;

import org.junit.Test;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SemanticAnalyzer;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.TypeErrorException;

/**
 * This tests the following parts:
 * Lexer -> Parser -> Semantic Analysis
 * 
 * The focus here lies on semantic analysis failures
 */
public class SemanticAnalysisTest {

	// "forward declare"
	static String mainC(String block) {
		return ParserTest.mainC(block);
	}

	@Test(expected = IllegalStateException.class)
	public void testDuplicateIds() {
		final String code = mainC("int a; int a;");
		analyze(code);
	}

	@Test
	public void testValidFunctionDef() {
		final String code = mainC("") + 
				"def void foo(int a){int b;}" + 
				"def void foo(real a){int b;}";
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
	public void testInvalidArtihmeticsOnStrings(){
		final String code = mainC("\"foo\" + \"foo\";");
		analyze(code);
	}

	@Test(expected = TypeErrorException.class)
	public void testInvalidArtihmeticsOnRecords(){
		final String code = mainC("record {int a; int b; } r; r + r;");
		analyze(code);
	}

	@Test(expected = TypeErrorException.class)
	public void testInvalidAssignOperand1() {
		final String code = mainC("bool a; a = !42;");
		analyze(code);
	}

	@Test(expected = TypeErrorException.class)
	public void testInvalidAssignOperand2() {
		final String code = mainC("bool b; int i; i = b;");
		analyze(code);
	}

	@Test(expected = TypeErrorException.class)
	public void testInvalidAssignOperand3() {
		final String code = mainC("int a; a = 0.0;");
		analyze(code);
	}

	@Test
	public void testRecordAsReturnType(){
		final String code = mainC("") + 
				"def record {int real; int imag;} foo() {" + 
					"record {int real; int imag;} myRecord; return myRecord;" + 
				"}";
		analyze(code);
	}

	@Test
	public void testMultiExpression() {
		String code = mainC("int a; a = 1 + 2 + 3;");
		analyze(code);
	}

	@Test
	public void testRecordBehaviour(){
		final String code = mainC("") + 
				"def int foobar(record {int r; int i;} t) {" + 
				"t.r = 1; t.i = 0;" +
				"}";
		analyze(code);
	}

	@Test
	public void testReturnRecordElement() { 
		String code = mainC("record {int r; int i;} r; r.r = 0; return r.r;");
		analyze(code);
	}

	@Test(expected = SemanticException.class)
	public void testMissingMain(){
		final String code = "def int foobar(){return 0;}";
		analyze(code);
	}

	@Test(expected = SemanticException.class)
	public void testReturnVoidInNonVoidFunction() {
		final String code = "def int main() { return; }";
		analyze(code);
	}

	@Test(expected = SemanticException.class)
	public void testReturnValueInVoidFunction() {
		final String code = mainC("") + "def void foo() { return 1; }";
		analyze(code);
	}

	@Test(expected = SemanticException.class)
	public void testInvalidPrintArgument() {
		final String code = mainC("record { int a; int b; } r; print r;");
		analyze(code);
	}

	@Test
	public void testFunctionCallAsOperand() {
		String code = "def int foo() { return 1; }" + mainC("int i; i = foo();");
		analyze(code);
	}

	@Test(expected = TypeErrorException.class)
	public void testVariableDeclaredVoid() {
		final String code = mainC("void v;");
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
