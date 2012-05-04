package parsetokdef;

import java.util.List;

import lexergen.Settings;

import org.junit.Assert;
import org.junit.Test;

import utils.IRule;

/**
 * 
 * @author benjamin
 */
public class ReadTokDefinitionTest {

	/**
	 * Test of readFile method, of class ReadTokDefinition.
	 */
	@Test
	public void testReadFile() throws Exception {
		Settings.readSettings();
		System.out.println(Settings.getRegularDefinitionFileName());
		String path = Settings.getDefaultTokenDef();
		ReadTokDefinition instance = new ReadTokDefinition();
		instance.readFile(path);
	}
	
	/**
	 * 
	 */
	@Test
	public void testRegex() throws Exception {
		List<IRule> rules = new ReadTokDefinition().getRules();
		String tokenType = rules.get(0).getTokenType();
		String tokenValue = rules.get(0).getTokenValue();
		System.out.println(rules.get(0));
		Assert.assertEquals("BRACKET", tokenType);
		Assert.assertEquals("(", tokenValue);
		tokenType = rules.get(5).getTokenType();
		tokenValue = rules.get(5).getTokenValue();
		System.out.println(rules.get(5));
		Assert.assertEquals("OP", tokenType);
		Assert.assertEquals("L", tokenValue);
	}
}
