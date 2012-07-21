package de.fuberlin.projecta;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

/**
 * Tests for more complex programs
 */
public class RealWorldTest {

	@Test
	public void testQuickSort() throws IOException {
		File file = new File("input/de/fuberlin/projecta/quickSort.lmb");
		String source = "";
		if (file.canRead()) {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				source += line + "\n";
			}
		}
		System.out.println(source);
		String output = CompilerTest.executeCode(source);
		assertEquals("", output);
	}

	@Test
	public void testFibonacciFunction() {
		String source = "def int main(){int x; x = fib(4); print x; return 0;}\n"
				+ "def int fib(int x){"
				+ "if(x == 0) return 0;"
				+ "if(x == 1) return 1;"
				+ "return fib(x-2) + fib(x-1);"
				+ "}\n";
		System.out.println(source);
		String output = CompilerTest.executeCode(source);
		assertEquals("3", output);
	}

	@Test
	public void testAddTwoFunctionReturnValues() {
		String source = ""
				+ "def int foo(int x){return x;}"
				+ "def int main() {int x; x = foo(1) + foo(2); print x; return 0;}";
		String output = CompilerTest.executeCode(source);
		assertEquals("3", output);
	}

	@Test
	public void testCompareTwoFunctionReturnValues() {
		String source = ""
				+ "def int foo(int x){return x;}"
				+ "def int main() {bool x; x = foo(1) <= foo(2); print x; return 0;}";
		String output = CompilerTest.executeCode(source);
		assertEquals("1", output); // true
	}

}
