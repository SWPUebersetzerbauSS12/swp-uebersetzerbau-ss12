package de.fuberlin.commons.parser;

/**
 * Simple class using strings as token representation
 */
public class StringSymbol implements ISymbol {
	
	String symbol;
	
	public StringSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String getName() {
		return this.symbol;
	}

}
