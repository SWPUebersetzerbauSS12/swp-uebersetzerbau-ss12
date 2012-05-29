package regextodfaconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

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
		assertEquals("(((a)(b))(c))", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("a{3}");
		assertEquals("(((a)(a))(a))", resultRegex);
		
		
		resultRegex = Regex.reduceAndBracketRegex("[a-d]");
		assertEquals("((((a)|(b))|(c))|(d))", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("[a-c-]");
		assertEquals("((((\\-)|(a))|(b))|(c))", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("[-a-c]");
		assertEquals("((((\\-)|(a))|(b))|(c))", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("[a-a]");
		assertEquals("(a)", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("[a-bc-d]");
		assertEquals("((((a)|(b))|(c))|(d))", resultRegex);
		
		try
		{
			resultRegex = Regex.reduceAndBracketRegex("[b-a]");
			fail("Exception should have been thrown");
		}
		catch (Exception e)
		{			
		}
		
		resultRegex = Regex.reduceAndBracketRegex("ba*");
		assertEquals("((b)((a)*))", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("a*");
		assertEquals("((a)*)", resultRegex);

		resultRegex = Regex.reduceAndBracketRegex("ab+");
		assertEquals("((a)((b)((b)*)))", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("a+");
		assertEquals("((a)((a)*))", resultRegex);
		
		try
		{
			resultRegex = Regex.reduceAndBracketRegex("a$");
			fail("Exception should have been thrown");
		}
		catch (Exception e)
		{			
		}
		
		resultRegex = Regex.reduceAndBracketRegex("a\\$");
		assertEquals("((a)(\\$))", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("(a|b)?");
		assertEquals("(()|((a)|(b)))", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("a{1,1}");
		assertEquals("(a)", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("(ab){1,2}");
		assertEquals("(((a)(b))|(((a)(b))((a)(b))))", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("(ab){0,2}");
		assertEquals("((()|((a)(b)))|(((a)(b))((a)(b))))", resultRegex);

		try
		{
			resultRegex = Regex.reduceAndBracketRegex("a{1,0}");
			fail("Exception should have been thrown");
		}
		catch (Exception e)
		{			
		}

		resultRegex = Regex.reduceAndBracketRegex("[.]");
		assertEquals("(\\.)", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("[}]");
		assertEquals("(\\})", resultRegex);
		
		resultRegex = Regex.reduceAndBracketRegex("[]]");
		assertEquals("(\\])", resultRegex);		
	}
}