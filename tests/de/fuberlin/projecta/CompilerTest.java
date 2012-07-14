package de.fuberlin.projecta;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.lexer.io.StringCharStream;

public class CompilerTest {

	// "forward declare"
	static String mainC(String block) {
		return ParserTest.mainC(block);
	}

	static String executeCode(String code) {
		ICharStream stream = new StringCharStream(code);
		String output = CompilerMain.execute(stream);
		return output;
	}

	@Test
	public void testPrint() {
		final String code = mainC("string s; s = \"foo\"; print s;");
		String output = executeCode(code);
		assertEquals(output, "foo");
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
	public void testLiteralComparison(){
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
	public void testFuncCallWithLiteralParameter(){
		final String code = "def int foo(int i){ return i;} def int main(){int i; i = foo(3)*foo(4); print i; return 0;}";
		String output = executeCode(code);
		assertEquals("12", output);
	}
	
	@Test
	public void testReturnWithBinaryOp(){
		final String code = "def int foo(int i){ return i+1;} def int main(){int i; i = foo(3); print i; return 0;}";
		String output = executeCode(code);
		assertEquals("4", output);
	}
	
	@Test
	public void testFuncCallWithBinaryOp(){
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
	public void testRecursiveFuncCall(){
		final String code = "def int foo(int i){if (i==0) {return 1;} return foo(i-1);} def int main(){int i; i = foo(3); print i; return 0;}";
		String output = executeCode(code);
		assertEquals("1", output);
	}

	@Test
	public void testIfElseReturnStatement() {
		final String code = mainC("int a; int b;  bool c; a = 1;  b = 2; c = true;\n" +
				"if (c) { print a; }\n" + 
				"else { print b; }");
		String output = executeCode(code);
		assertEquals("1", output);
	}

}
