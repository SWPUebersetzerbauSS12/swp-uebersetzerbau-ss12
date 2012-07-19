package de.fuberlin.projecta;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for more complex programs
 */
public class RealWorldTest {

	@Test
	public void fib() {
		String source = "def int main(){int x; x = fib(4); print x; return 0;}\n" +
		"def int fib(int x){" +
			"if(x == 0) return 0;"+
			"if(x == 1) return 1;"+
			"return fib(x-2) + fib(x-1);"+
		"}\n";
		String output = CompilerTest.executeCode(source);
		assertEquals("3", output);
	}
	
	@Test
	public void addTwoFunctions(){
		String source = ""+
				"def int foo(int x){return x;}"+
				"def int main() {int x; x = foo(1) + foo(2); print x; return 0;}";
		String output = CompilerTest.executeCode(source);
		assertEquals("3", output);
	}
	
	@Test
	public void compareTwoFunctions(){
		String source = ""+
			"def int foo(int x){return x;}"+
			"def int main() {bool x; x = foo(1) <= foo(2); print x; return 0;}";
		String output = CompilerTest.executeCode(source);
		assertEquals("1", output); // true
	}
}
