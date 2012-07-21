package de.fuberlin.projecta;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Categories.ExcludeCategory;

import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.lexer.io.StringCharStream;

/**
 * This tests the following parts: Lexer -> Parser -> Semantic analysis -> Code
 * generation
 * 
 * This is a complete test for the whole compiler (frontend+backend)
 */
public class CompilerTest {

	// "forward declare"
	static String mainC(String block) {
		return ParserTest.mainC(block);
	}

	static String executeCode(String code) {
		ICharStream stream = new StringCharStream(code);
		String output = CompilerMain.execute(stream, /* verbose = */true);
		return output;
	}

	@Test
	public void testPrint() {
		final String code = mainC("string s; s = \"foo\"; print s;");
		String output = executeCode(code);
		assertEquals(output, "foo");
	}

	@Test
	public void testImplicitReturnValueOnIntegerAssignment() {
		String code = "def int foo() { int i; i = 1; }"
				+ mainC("int j; j = foo(); print j; return 0;");
		String output = executeCode(code);
		assertEquals("1", output);
	}

	@Test
	public void testAdd() {
		final String code = mainC("int a; a = 1 + 1; print a;");
		String output = executeCode(code);
		assertEquals(output, "2");
	}

	@Test
	public void testFuncReturnInt() {
		final String code = "def int foo(){return 0;} def int main(){int i; i = foo(); print i; return 0;}";
		String output = executeCode(code);
		assertEquals(output, "0");
	}

	@Test
	public void testUnimplicitVarIncrementingInFuncAssign() {
		final String code = "def int foo(){return 0;} def int main(){int i; i = foo(); print i; return 0;}";
		String output = executeCode(code);
		assertEquals(output, "0");
	}

	@Test
	public void testImplicitVarIncrementingInFuncAssign() {
		final String code = "def int foo(){return 0;} def int main(){foo(); return 0;}";
		String output = executeCode(code);
		assertEquals(output, "");
	}

	@Test
	public void testIfOnTrueWithIntegerComparison() {
		final String code = "def int main(){int i; int j; i = 1; j = 2; if(i <= j) { print i; print j;} return 0;}";
		String output = executeCode(code);
		assertEquals(output, "12");
	}

	@Test
	public void testIfElseOnTrueWithIntegerComparison() {
		final String code = "def int main(){int i; int j; i = 1; j = 2; if(i <= j) { print i; print j;} else {print j; print i;} return 0;}";
		String output = executeCode(code);
		assertEquals(output, "12");
	}

	@Test
	public void testIfOnFalseWithIntegerComparison() {
		final String code = "def int main(){int i; int j; i = 1; j = 2; if(i >= j) { print i; print j;} return 0;}";
		String output = executeCode(code);
		assertEquals(output, "");
	}

	@Test
	public void testIfElseOnFalseWithIntegerComparison() {
		final String code = "def int main(){int i; int j; i = 1; j = 2; if(i >= j) { print i; print j;} else {print j; print i;} return 0;}";
		String output = executeCode(code);
		assertEquals(output, "21");
	}

	@Test
	public void testLiteralComparison() {
		final String code = "def int main(){bool a; bool b; bool c; bool d; a = 3 == 4; b = 3 == 3; c = 3 != 4; d = 3 != 3; print a; print b; print c; print d; return 1;}";
		String output = executeCode(code);
		assertEquals(output, "0110");
	}

	@Test
	public void testWhile() {
		final String code = mainC("int i; int j; i = 0; j = 10; while(i < j) {print i; i = j;}");
		String output = executeCode(code);
		assertEquals(output, "0");
	}

	@Test
	public void testParameterWithoutLoading() {
		final String code = "def int foo(int i) { int j; j = i + i; return j;} def int main(){int i; i = 3; i = foo(i); print i; return 0;}";
		String output = executeCode(code);
		assertEquals(output, "6");
	}

	@Test
	public void testFuncCallWithLiteralParameter() {
		final String code = "def int foo(int i){ return i;} def int main(){int i; i = foo(3)*foo(4); print i; return 0;}";
		String output = executeCode(code);
		assertEquals("12", output);
	}

	@Test
	public void testReturnWithBinaryOp() {
		final String code = "def int foo(int i){ return i+1;} def int main(){int i; i = foo(3); print i; return 0;}";
		String output = executeCode(code);
		assertEquals("4", output);
	}

	@Test
	public void testFuncCallWithBinaryOp() {
		final String code = "def int foo(int i){ return i;} def int main(){int i; i = foo(3+4); print i; return 0;}";
		String output = executeCode(code);
		assertEquals("7", output);
	}

	@Test
	public void testMultiExpression() {
		String code = mainC("int a; a = 1 + 2 + 3; print a;");
		String output = executeCode(code);
		assertEquals("6", output);
	}

	@Test
	public void testRecursiveFuncCall() {
		final String code = "def int foo(int i){if (i==0) {return 1;} return foo(i-1);} def int main(){int i; i = foo(3); print i; return 0;}";
		String output = executeCode(code);
		assertEquals("1", output);
	}

	@Test
	public void testIfElsePrintStatement() {
		final String code = mainC("int a; int b;  bool c; a = 1;  b = 2; c = true;\n"
				+ "if (c) { print a; }\n" + "else { print b; }");
		String output = executeCode(code);
		assertEquals("1", output);
	}

	@Test
	public void testIfElseReturnStatement() {
		final String code = "def int foo() { int a; int b;  bool c; a = 1;  b = 2; c = true;\n"
				+ "if (c) { return a; }\n"
				+ "else { return b; }\n"
				+ "}\n"
				+ mainC("int a; a = foo(); print a;");
		System.out.println(code);
		String output = executeCode(code);
		assertEquals("1", output);
	}

	@Test
	public void testRecordDeclaration() {
		final String code = mainC("record {int i; int j;} myRecord;");
		System.out.println(code);
		String output = executeCode(code);
		assertEquals("", output);
	}

	@Test
	public void testRecordFuncCall() {
		final String code = "def int foo(record {int i;} a) { int x; x = a.i; print x; return a.i + 1;} "
				+ "def int main(){ record {int i;} a; int bla; a.i = 3; bla = foo(a); print bla; return 0;}";
		System.out.println(code);
		String output = executeCode(code);
		assertEquals("34", output);

	}

	@Test
	public void testNestedRecords() {
		final String code = "def real main(){record{real x; record{int c;}b;}a;"
				+ "int d; a.b.c = 3; a.x = 3.14; d = a.b.c; print a.x; return a.x;}";
		System.out.println(code);
		String output = executeCode(code);
		assertEquals("3.140000", output);
	}

	/*
	 * @Test public void testUnaryOpInIfStatement() { final String code =
	 * mainC("bool b; int i; b = false; if (!b) { i = 1; } else { i = 0; } print i;"
	 * ); System.out.println(code); String output = executeCode(code);
	 * assertEquals("1", output); }
	 */

	@Test
	public void testNestedRecords2() {
		final String code = mainC("record { real x; record {real x; record { int i;} inner; } middle; } outer;"
				+ "outer.middle.inner.i=3; print outer.middle.inner.i;");
		System.out.println(code);
		String output = executeCode(code);
		assertEquals("3", output);
	}

	@Test
	public void testArray() {
		final String code = mainC("int[10] test; int i; int tmp;" + "i = 0; "
				+ "while (i < 10){" + "test[i] = i;" + "tmp = test[i];"
				+ "print tmp;" + "i=i+1; " + "}");
		System.out.println(code);
		String output = executeCode(code);
		assertEquals("0123456789", output);
	}

	@Test
	public void testArrayFuncCall() {
		final String code = "def int fun(int[30] x) { return x[1]; } "
				+ "def int main(){int[30] x; int i; x[1] = 1; i = fun(x); print i; return 0;}";
		System.out.println(code);
		String output = executeCode(code);
		assertEquals("1", output);
	}

	@Test
	public void testMultiDimensionalArrays() {
		final String code = mainC("int[3][5] test; int i; int j; int tmp;"
				+ "i = 0;" + "while (i < 3){" + "j = 0;" + "while(j < 5){"
				+ "test[i][j] = i+j;" + "j=j+1;" + "}" + "i=i+1; " + "} "
				+ "i = 0;" + "while (i < 3){" + "j = 0;" + "while(j < 5){"
				+ "tmp = test[i][j];" + "print tmp;" + "j=j+1;" + "}"
				+ "i=i+1; " + "} "

		);
		System.out.println(code);
		String output = executeCode(code);
		assertEquals("012341234523456", output);
	}

	@Test
	public void testVoidMain() {
		String code = "def void main() { string s; s = \"foo\"; print s; return; }";
		String output = executeCode(code);
		assertEquals("foo", output);
	}

	@Test
	public void testDoWhileStatement() {
		String code = mainC("int i; i = 0; do {" + "print i;" + "i = i+1;"
				+ "} while (i < 5);");
		String output = executeCode(code);
		System.out.println(output);
		assertEquals("01234", output);
	}

	@Test
	public void testPrintNestedArray() {
		String code = "def void foo(int[30][50] array, int i)"
				+ "{print array[i][0];} "
				+ mainC("int[30][50] array; int i; array[0][0] = 0; i = 0; foo(array,i);");
		String output = executeCode(code);
		System.out.println(output);
		assertEquals("0", output);
	}

	@Test 
	public void testPrintBasicParameter() {
		String code = "def void foo(int i, real r, string s){" + 
				"print i; print r; print s; return;}" + 
				"def void main(){" + 
				"int a; real b; string c; a = 2; b = 1.0; c = \"bar\"; foo(a,b,c);}";
		String output = executeCode(code);
		System.out.println(output);
		assertEquals("21.0bar", output);
	}
	
	@Test
	public void testBasicParameter(){
		String code = "def void foo(int i, real r, string s){" + 
				"int a; a = i; a = i+1; print i; print r; print s;}" + 
				"def void main(){" + 
				"int a; real b; string c; a = 2; b = 1.0; c = \"bar\"; foo(a,b,c);}";
		String output = executeCode(code);
		System.out.println(output);
		assertEquals("21.0bar", output);
	}
	
	@Test
	public void testRealEquations(){
		String code = "def void main(){real r; real x; r = 1.0; x = 2.0; r = r + x; print r;}";
		String output = executeCode(code);
		System.out.println(output);
		assertEquals("3.000000", output);
	}
	
	@Test(expected= SemanticException.class)
	public void testAssignValueToParameterVariable(){
		String code = "def void foo(int i){i = 4;}def void main(){foo(3);}";
		String output = executeCode(code);
		System.out.println(output);
	}
}
