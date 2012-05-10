import lexer.io.StringCharStream;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class LexerIoTest {

	@Test
	public void test() {
		String data = "def int function();";
		StringCharStream stream = new StringCharStream(data);

		assertEquals(stream.getNextChars(3), "def");
		assertEquals(stream.consumeChars(3), 3);
		assertEquals(stream.getOffset(), 3);
		assertEquals(stream.getNextChars(1), " ");
		assertEquals(stream.consumeChars(5), 5);
		assertEquals(stream.isEmpty(), false);
		assertEquals(stream.getNextChars(20), "function();");
		assertEquals(stream.consumeChars(20), 11);
		assertEquals(stream.getOffset(), data.length());
		assertEquals(stream.isEmpty(), true);
	}

}
