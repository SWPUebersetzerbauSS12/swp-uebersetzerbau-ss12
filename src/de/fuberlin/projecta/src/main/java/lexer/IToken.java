package lexer;

public interface IToken {

	public enum TokenType {
		/** relational operators <(LT), <=(LE), ==(EQ), !=(NE), >(GT), >=(GE) */
		OP_LT, OP_LE, OP_EQ, OP_NE, OP_GT, OP_GE,
		/** ||(OR), &&(AND), !(NOT) */
		OP_OR, OP_AND, OP_NOT,
		/** Arithmetic operators +(ADD) -(SUB) *(MUL) /(DIV) -(NEG) */
		OP_ADD, OP_SUB, OP_MUL, OP_DIV, OP_NEG,
		/** Assignment (=) operator */
		OP_ASSIGN,
		/** Comma (,) operator */
		OP_COMMA,
		/** Dot (.) operator */
		OP_DOT,
		/** Semicolon (;) operator */
		OP_SEMIC,
		/** Other reserverd key words */
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
		/** "(" */
		LPAREN,
		/** ")" */
		RPAREN,
		/** "[" */
		LBRACKET,
		/** "]" */
		RBRACKET,
		/** "{" */
		LBRACE,
		/** "}" */
		RBRACE,
		/** End-of-file marker */
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
