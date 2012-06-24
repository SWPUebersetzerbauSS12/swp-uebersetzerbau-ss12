package de.fuberlin.projecta;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.lexer.io.StringCharStream;

public class CompilerTest {

	static String executeCode(String code) {
		ICharStream stream = new StringCharStream(code);
		String output = CompilerMain.execute(stream);
		return output;
	}

	@Test
	public void testCompiler() {
		final String code = "def int main() { string s; s = \"foo\"; print s; return 0;}";
		String output = executeCode(code);
		assertEquals(output, "foo");
	}

}
