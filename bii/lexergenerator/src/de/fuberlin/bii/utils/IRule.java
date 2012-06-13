package de.fuberlin.bii.utils;

import tokenmatcher.attributes.Attribute;

public interface IRule {
	
	public String getRegexp();

	public String getTokenType();

	public Attribute getTokenValue();
}
