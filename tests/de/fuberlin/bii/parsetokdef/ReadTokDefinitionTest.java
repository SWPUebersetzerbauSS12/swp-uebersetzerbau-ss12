package de.fuberlin.bii.parsetokdef;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.fuberlin.bii.tokenmatcher.attributes.Attribute;
import de.fuberlin.bii.utils.IRule;

/**
 * Test-Klasse für die ReadTokDefinition-Klasse.
 * @author Benjamin Weißenfels
 */
public class ReadTokDefinitionTest {

	/**
	 * Test of readFile method, of class ReadTokDefinition.
	 */
	@Test
	public void testReadFile() throws Exception {
		File rdFile = new File("tests/resources/de/fuberlin/bii/def/parsetokdef/test.rd");
		ReadTokDefinition instance = new ReadTokDefinition(rdFile);
		instance.readFile(rdFile);
	}

	@Test
	public void testRegex() throws Exception {
		File rdFile = new File("tests/resources/de/fuberlin/bii/def/parsetokdef/test.rd");
		List<IRule> rules = new ReadTokDefinition(rdFile).getRules();
		
		String tokenType = rules.get(0).getTokenType();
		Attribute tokenValue = rules.get(0).getTokenValue();
		String tokenRegex = rules.get(0).getRegexp();
		Assert.assertEquals("BRACKET", tokenType);
		Assert.assertEquals("(", tokenValue.toString());
		Assert.assertEquals("\\(", tokenRegex);
		
		tokenType = rules.get(5).getTokenType();
		tokenValue = rules.get(5).getTokenValue();
		Assert.assertEquals("OP", tokenType);
		Assert.assertEquals("L", tokenValue.toString());
	}
}
