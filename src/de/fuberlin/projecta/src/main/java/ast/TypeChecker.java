package ast;

import lexer.IToken.TokenType;

public class TypeChecker {

	public static boolean isNumeric(TokenType type) {
		// TODO: Do we have other types here?
		return (type == TokenType.INT_LITERAL || type == TokenType.REAL_LITERAL);
	}

	public static TokenType max(TokenType t1, TokenType t2) {
		if (!isNumeric(t1) || !isNumeric(t2))
			return null;

		if (t1 == TokenType.REAL_LITERAL || t2 == TokenType.REAL_LITERAL)
			return TokenType.REAL_LITERAL;

		if (t1 == TokenType.INT_LITERAL || t2 == TokenType.INT_LITERAL)
			return TokenType.INT_LITERAL;

		return null;
	}

}
