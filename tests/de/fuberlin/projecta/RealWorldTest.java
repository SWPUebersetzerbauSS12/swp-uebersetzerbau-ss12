package de.fuberlin.projecta;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RealWorldTest {
	@Test
	public void fib() {
		String source = "def int main(){int x; x = fib(4); print x; return 1; }\n" +
		"def int fib(int x){" +
			"if(x == 0) return 0;"+
			"if(x == 1) return 1;"+
			"return fib(x-2) + fib(x-1);"+
		"}\n";
		String output = CompilerTest.executeCode(source);
		assertEquals(output, "3");
	}
}
