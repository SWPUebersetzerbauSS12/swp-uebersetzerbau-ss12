package de.fuberlin.projectci.extern;

public interface ILexer {

	/**
	 * @return the next Token
	 * @throws SyntaxErrorException
	 */
	Token getNextToken();
}
