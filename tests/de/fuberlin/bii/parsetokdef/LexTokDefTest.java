package de.fuberlin.bii.parsetokdef;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.fuberlin.bii.tokenmatcher.attributes.Attribute;
import de.fuberlin.bii.utils.IRule;

/**
 * Test-Klasse für die {@link LexTokDef} Klasse.
 * 
 * @author Benjamin Weißenfels
 */
public class LexTokDefTest {

	/**
	 * Test of readFile method, of class ReadTokDefinition.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testReadFile() throws Exception {
		File rdFile = new File(
				"tests/resources/de/fuberlin/bii/def/parsetokdef/test.rd");
		ReadTokDefAbstract instance = new LexTokDef(rdFile);
		instance.readFile(rdFile);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testRegex() throws Exception {
		File rdFile = new File(
				"tests/resources/de/fuberlin/bii/def/parsetokdef/test.rd");
		List<IRule> rules = new LexTokDef(rdFile).getRules();
		System.out.println(rules);

		String tokenType = rules.get(0).getTokenType();
		String tokenRegex = rules.get(0).getRegexp();
		Assert.assertEquals("(", tokenType);

		Assert.assertEquals("\\(", tokenRegex);
		tokenType = rules.get(5).getTokenType();
		Assert.assertEquals("]", tokenType);

		tokenType = rules.get(rules.size() - 3).getTokenType();
		tokenRegex = rules.get(rules.size() - 3).getRegexp();
		Assert.assertEquals("num", tokenType);
		Assert.assertEquals("0|([1-9]+[0-9]*)", tokenRegex);
	}
}
