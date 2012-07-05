package de.fuberlin.commons.parser;

import de.fuberlin.commons.lexer.ILexer;


public interface IParser {

	public final static String TOKEN_VALUE = "TokenValue";

	/**
	 * Interface for triggering a parse
	 *
	 * @throws RuntimeException
	 */
	public ISyntaxTree parse(ILexer lexer, String grammar);

}
