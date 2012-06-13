package de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree;

import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.Symbol;


public class RegularExpressionElement<StatePayloadType> extends Symbol<Character, StatePayloadType> {


	public RegularExpressionElement( Character value) {
		super( value);
	}
	
	
	public RegularExpressionElement( Character value, StatePayloadType payload) {
		super( value, payload);
	}
	
	
}
