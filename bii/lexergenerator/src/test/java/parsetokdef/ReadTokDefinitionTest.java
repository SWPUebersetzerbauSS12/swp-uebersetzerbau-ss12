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
		System.out.println("readFile");
		@SuppressWarnings("unused")
		ReadTokDefinition instance = new ReadTokDefinition(
				Settings.getDefaultTokenDef());
	}

	/**
	 * 
	 */
	@Test
	public void testRegex() throws Exception {
		List<IRule> rules = new ReadTokDefinition(null).getRules();
		String tokenType = rules.get(0).getTokenType();
		System.out.println(rules.get(0));
		Assert.assertEquals("BRACKET", tokenType);
		tokenType = rules.get(5).getTokenType();
		System.out.println(rules.get(5));
		Assert.assertEquals("OP", tokenType);
	}
}
