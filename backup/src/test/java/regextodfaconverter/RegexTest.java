package regextodfaconverter;

import org.junit.Assert;
import org.junit.Test;

import regextodfaconverter.Regex;

/**
 * Test-Klasse für die Regex-Klasse.
 * @author Daniel Rotar
 * @author Wojciech Lukasiewicz
 *
 */
public class RegexTest {
	
	/**
	 * Test of reduceAndBracketRegex method, of class Regex.
	 */
	@Test
	public void testReduceAndBracketRegex() throws Exception {
		String resultRegex;
		
		resultRegex = Regex.reduceAndBracketRegex("abc");
		Assert.assertEquals("(((a)(b))(c))", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("a{3}");
		Assert.assertEquals("(((a)(a))(a))", resultRegex);
		
		//TODO @Wojciech: weitere Testfälle implementieren.
	}
}
