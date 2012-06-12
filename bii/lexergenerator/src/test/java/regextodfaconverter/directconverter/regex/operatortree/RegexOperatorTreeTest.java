package regextodfaconverter.directconverter.regex.operatortree;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import regextodfaconverter.directconverter.PositionToPayloadMap;
import tokenmatcher.StatePayload;


public class RegexOperatorTreeTest {

	@Test
	public void test() {

		String regex = "(a|b)*abb";
		int regexLength = regex.length();

		RegularExpressionElement[] regularExpression = new RegularExpressionElement[regexLength];
		for ( int i = 0; i < regexLength; i++) {
			regularExpression[i] = new RegularExpressionElement( regex.charAt( i), null);
		}

		RegexOperatorTree regexTree = null;
		Boolean noExceptionsOccur = true;
		try {
			regexTree = new RegexOperatorTree( regularExpression);
		} catch ( Exception e) {
			noExceptionsOccur = false;
		}
		Assert.assertTrue( noExceptionsOccur);
		
		System.out.println( regexTree);
	}

}
