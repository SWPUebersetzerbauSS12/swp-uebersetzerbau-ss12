package regextodfaconverter.directconverter.regex.operatortree;

import regextodfaconverter.directconverter.lr0parser.grammar.Symbol;
import tokenmatcher.StatePayload;
import utils.Test;


public class RegularExpressionElement<StatePayloadType> extends Symbol<Character, StatePayloadType> {


	public RegularExpressionElement( Character value) {
		super( value);
	}
	
	
	public RegularExpressionElement( Character value, StatePayloadType payload) {
		super( value, payload);
	}
	
	
}
