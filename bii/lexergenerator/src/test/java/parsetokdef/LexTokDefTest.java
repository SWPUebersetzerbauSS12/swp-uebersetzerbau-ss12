package parsetokdef;

import java.util.List;

import lexergen.Settings;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import utils.IRule;

/**
 * Test-Klasse für die ReadTokDefinition-Klasse.
 * 
 * @author Benjamin Weißenfels
 */
public class LexTokDefTest {

	@Before
	public void readSettings() {
		Settings.readSettings();
	}

	/**
	 * Test of readFile method, of class ReadTokDefinition.
	 */
	@Test
	public void testReadFile() throws Exception {
		String path = Settings.getDefaultTokenDef();
		ReadTokDefinition instance = new ReadTokDefinition();
		instance.readFile(path);
	}

	@Test
	public void testRegex() throws Exception {
		String path = null;
		List<IRule> rules = new ReadTokDefinition(path).getRules();

		String tokenType = rules.get(0).getTokenType();
		String tokenValue = rules.get(0).getTokenValue();
		String tokenRegex = rules.get(0).getRegexp();
		Assert.assertEquals("BRACKET", tokenType);
		Assert.assertEquals("(", tokenValue);
		Assert.assertEquals("\\(", tokenRegex);

		tokenType = rules.get(3).getTokenType();
		tokenValue = rules.get(3).getTokenValue();
		tokenRegex = rules.get(3).getRegexp();
		Assert.assertEquals("BRACKET", tokenType);
		Assert.assertEquals("}", tokenValue);
		Assert.assertEquals("\\}", tokenRegex);

		tokenType = rules.get(5).getTokenType();
		tokenValue = rules.get(5).getTokenValue();
		Assert.assertEquals("OP", tokenType);
		Assert.assertEquals("L", tokenValue);
	}
}
