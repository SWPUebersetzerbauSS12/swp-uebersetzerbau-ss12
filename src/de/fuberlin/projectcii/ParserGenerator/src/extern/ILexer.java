package de.fuberlin.projectcii.ParserGenerator.src.extern;

public interface ILexer {

	/**
	 * Interface for accessing the token stream
	 * 
	 * @return The next Token
	 * @throws RuntimeException
	 */
	IToken getNextToken();

}
