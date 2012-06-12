package de.fuberlin.commons.lexer;

/**
 * Special token types for the 'Lammbock' language defined in an enum. 
 * The enum names define the strings that are expected in IToken.getType()
 * for this particular language. You're not required to actually use these enums,
 * they're here for type safety for the ProjectA-Team.
 * 
 * @example
 * For '+' IToken.getType() should return 'OP_AND'
 * 
 * @see IToken.getType()
 */
public enum TokenType {

	/** relational operators <(LT), <=(LE), ==(EQ), !=(NE), >(GT), >=(GE) */
	OP_LT, OP_LE, OP_EQ, OP_NE, OP_GT, OP_GE,
	/** ||(OR), &&(AND), !(NOT) */
	OP_OR, OP_AND, OP_NOT,
	/** Plus (+) operator */
	OP_ADD,
	/** Minus (-) operator. Both for unary and binary operations */
	OP_MINUS,
	/** Multiplication (*) operator */
	OP_MUL,
	/** Division (/) operator */
	OP_DIV,
	/** Assignment (=) operator */
	OP_ASSIGN,
	/** Comma (,) operator */
	OP_COMMA,
	/** Dot (.) operator */
	OP_DOT,
	/** Semicolon (;) operator */
	OP_SEMIC,

	/** Other reserverd key words */
	IF, THEN, ELSE, WHILE, DO, BREAK, RETURN, PRINT,
	/** Function definition */
	DEF,
	/** Record keyword */
	RECORD,
	/** Identifier */
	ID,

	/** Bool (bool) type */
	BOOL_TYPE,
	/** String (string) type */
	STRING_TYPE,
	/** Integer (int) type) */
	INT_TYPE,
	/** Real (real) type */
	REAL_TYPE,

	/** Boolean literal */
	BOOL_LITERAL,
	/** String constant */
	STRING_LITERAL,
	/** Integer number */
	INT_LITERAL,
	/** Real number */
	REAL_LITERAL,

	/**
	 * For Java-style comments
	 * 
	 * @note Review if we really need them
	 * @note The parser-generator group asked for those types
	 */
	MULTILINE_COMMENT_START, MULTILINE_COMMENT_END, SINGLELINE_COMMENT,

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
