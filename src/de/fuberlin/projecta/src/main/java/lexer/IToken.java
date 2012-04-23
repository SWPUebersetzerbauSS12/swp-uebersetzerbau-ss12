package lexer;

public interface IToken {

	public enum TokenType {
		/** relational operators <(LT), <=(LE), ==(EQ), !=(NE), >(GT), >=(GE) */
		RELOP,
		/** ||(OR), &&(AND), !(NOT) */
		BOOLOP,
		/** Arithmetic Operator +(SUM) -(SUB) *(MUL) /(DIV) -(NEG) */
		ARITHOP,
		/** Assignment operator */
		ASSIGN,
		/** Other reserverd words */
		IF, THEN, ELSE, WHILE, DO, BREAK,
		RETURN, PRINT,
		/** Function definition */
		DEF,
		/** Identifier */
		ID,
		/** String constant */
		STRING,
		/** Integer number */
		INT,
		/** Real number */
		REAL,
		/** For array definitions, this marks the field count */
		NUM,
		/** Terminal symbols */
		LPAREN, RPAREN,
		LBRACKET, RBRACKET,
		LBRACE, RBRACE,
		/** Comma (,) operator */
		COMMA,
		/** Dot (.) operator */
		DOT,
		/** Semicolon (;) operator */
		SEMIC,
		/** End of file */
		EOF
	}

	/**
	 * Get the type of this Token
	 * 
	 * @return Token type
	 */
	TokenType getType();

	/**
	 * Get the Token attribute value
	 * 
	 * E.g. for a Token of type REAL this can be "0.0"
	 * 
	 * @return Attribute value
	 */
	String getAttribute();

	/**
	 * Get the start offset of this Token's attribute
	 * 
	 * @note The position is relative to the beginning of the line
	 * 
	 * @return Start offset
	 */
	int getOffset();

	/**
	 * Get the line number of this Token's attribute
	 * 
	 * @return End offset
	 */
	int getLineNumber();
}
