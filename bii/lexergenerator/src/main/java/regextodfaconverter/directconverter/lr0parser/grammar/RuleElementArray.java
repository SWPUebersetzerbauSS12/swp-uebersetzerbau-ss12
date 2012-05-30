package regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.ArrayList;

public class RuleElementArray extends ArrayList<RuleElement> implements RuleElementSequenz {
	
	public RuleElementArray() {
	  super();
	}
	
	
	public static <T extends Comparable<T>> RuleElementArray toRuleElementArray( T ... ts) {
		RuleElementArray result = new RuleElementArray();
		for ( T t : ts) {
			result.add( new Terminal<T>( t));
		}
		return result;
	}
	
}
