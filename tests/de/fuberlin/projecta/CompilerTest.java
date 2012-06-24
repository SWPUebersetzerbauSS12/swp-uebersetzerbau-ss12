package de.fuberlin.projecta;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.lexer.io.StringCharStream;

public class CompilerTest {

	static String mainC(String block) {
		String code = "def int main() { ";
		code += block;
		code += "return 0; }";
		return code;
	}

	static String executeCode(String code) {
		ICharStream stream = new StringCharStream(code);
		String output = CompilerMain.execute(stream);
		return output;
	}

	@Test
	public void testPrint() {
		final String code = mainC( "string s; s = \"foo\"; print s;" );
		String output = executeCode(code);
		assertEquals(output, "foo");
	}

	@Test
	public void testAdd() {
		final String code = mainC("int a; a = 1 + 1; print a;");
		String output = executeCode(code);
		assertEquals(output, "2");
	}

}
