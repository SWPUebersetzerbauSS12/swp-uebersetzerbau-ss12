package de.fuberlin.commons.lexer;

public interface ILexer {

	/**
	 * Interface for accessing the token stream
	 * 
	 * @return The next Token
	 * @throws RuntimeException
	 */
	IToken getNextToken();

	/**
	 * Setzt die Position im Quellprogramm auf die Startposition zurück.
	 * @throws RuntimeException
	 */
	void reset();

}
