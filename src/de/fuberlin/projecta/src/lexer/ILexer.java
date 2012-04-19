package lexer;

public interface ILexer {

	/**
	 * @return next token from source file
	 */
	public Token getNextToken() throws SyntaxErrorException;
	
	
	/**
	 * 
	 * @return current line number from which the last token was taken
	 * 
	 * when no token was taken yet return value is 1
	 */
	public int getLineNumber();

}
