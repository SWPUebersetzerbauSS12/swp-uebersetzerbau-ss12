package de.fuberlin.commons.parser;

import de.fuberlin.commons.lexer.ILexer;


public interface IParser {

	/**
	 * Interface for triggering a parse
	 *
	 * @throws RuntimeException
	 */
	public ISyntaxTree parse(ILexer lexer, String grammar);

}
