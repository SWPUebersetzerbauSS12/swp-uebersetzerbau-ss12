package lexer;

public interface ILexer {

	Token getNextToken() throws SyntaxErrorException;

}
