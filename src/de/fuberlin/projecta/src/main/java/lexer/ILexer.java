package lexer;

public interface ILexer {

	/**
	 * Interface for accessing the token stream
	 * 
	 * @return the next Token
	 * @throws SyntaxErrorException
	 */
	IToken getNextToken();

}
