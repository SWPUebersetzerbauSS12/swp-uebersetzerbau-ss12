package ast;

import lexer.IToken.TokenType;

public class TypeChecker {

	public static boolean isNumeric(TokenType type) {
		// TODO: Do we have other types here?
		return (type == TokenType.INT || type == TokenType.REAL);
	}

	public static TokenType max(TokenType t1, TokenType t2) {
		if (!isNumeric(t1) || !isNumeric(t2))
			return null;

		if (t1 == TokenType.REAL || t2 == TokenType.REAL)
			return TokenType.REAL;

		if (t1 == TokenType.INT || t2 == TokenType.INT)
			return TokenType.INT;

		return null;
	}

}
