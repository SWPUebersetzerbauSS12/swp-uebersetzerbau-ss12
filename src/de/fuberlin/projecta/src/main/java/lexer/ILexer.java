package lexer;

public interface ILexer {

	/**
	 * @return the next Token
	 * @throws SyntaxErrorException
	 */
	IToken getNextToken();
}
