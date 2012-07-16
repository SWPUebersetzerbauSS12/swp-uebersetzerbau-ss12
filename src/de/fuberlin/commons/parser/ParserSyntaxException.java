package de.fuberlin.commons.parser;

public class ParserSyntaxException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public ParserSyntaxException(String unexpectedToken, int line, int column,String expectedTokens) {
		super("Parser: Syntax Error unexpected Token "+unexpectedToken+" in line "+line+":"+column+" Expected Tokens: "+expectedTokens);
	}
}
