package parsetokdef;

import java.util.List;

import lexergen.Settings;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tokenmatcher.attributes.Attribute;
import utils.IRule;

/**
 * Test-Klasse für die {@link LexTokDef} Klasse.
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
		ReadTokDefAbstract instance = new LexTokDef();
		instance.readFile(Settings.getRegularDefinitionFileName());
	}

	@Test
	public void testRegex() throws Exception {

		String path = null;
		List<IRule> rules = new LexTokDef().getRules();

		printRules(rules);

		String tokenType = rules.get(0).getTokenType();
		Attribute tokenValue = rules.get(0).getTokenValue();
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

	private void printRules(List<IRule> rules) {
		System.out.println("Try to reading default Tokendefinition:");
		for (IRule r : rules)
			System.out.println(r);
	}
}
