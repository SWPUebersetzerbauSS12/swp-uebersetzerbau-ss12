package de.fuberlin.projectci.extern;

import de.fuberlin.projectci.grammar.Grammar;

public interface IToken {

	public enum TokenType {
		/** relational operators <(LT), <=(LE), ==(EQ), !=(NE), >(GT), >=(GE) */
		OP_LT("<"), OP_LE("<="), OP_EQ("=="), OP_NE("!="), OP_GT(">"), OP_GE(">="),
		/** ||(OR), &&(AND), !(NOT) */
		OP_OR("||"), OP_AND("||"), OP_NOT("!"),
		/** Plus (+) operator */
		OP_ADD("+"),
		/** Minus (-) operator. Both for unary and binary operations */
		OP_MINUS("-"),
		/** Multiplication (*) operator */
		OP_MUL("*"),
		/** Division (/) operator */
		OP_DIV("/"),
		/** Assignment (=) operator */
		OP_ASSIGN("="),
		/** Comma (,) operator */
		OP_COMMA(","),
		/** Dot (.) operator */
		OP_DOT("."),
		/** Semicolon (;) operator */
		OP_SEMIC(";"),

		/** Other reserverd key words */
		IF("if"), THEN("then"), ELSE("else"), WHILE("while"), DO("do"), BREAK("break"), RETURN("return"), PRINT("print"),
		/** Function definition */
		DEF("def"),
		/** Record keyword */
		RECORD("record"),
		/** Identifier */
		ID("id"),

		/** Bool (bool) type */
		BOOL_TYPE("bool"),
		/** String (string) type */
		STRING_TYPE("string"),
		/** Integer (int) type) */
		INT_TYPE("int"),
		/** Real (real) type */
		REAL_TYPE("float"),

		/** Boolean literal */
		BOOL_TRUE("true"),
		BOOL_FALSE("false"),
		/** String constant */
		STRING_LITERAL("string"),
		/** Integer number */
		INT_LITERAL("num"),
		/** Real number */
		REAL_LITERAL("real"),

		/**
		 * For Java-style comments
		 * 
		 * @note Review if we really need them
		 * @note The parser-generator group asked for those types
		 */
		MULTILINE_COMMENT_START("/*"), MULTILINE_COMMENT_END("*/"), SINGLELINE_COMMENT("//"),

		/** "(" */
		LPAREN("("),
		/** ")" */
		RPAREN(")"),
		/** "[" */
		LBRACKET("["),
		/** "]" */
		RBRACKET("]"),
		/** "{" */
		LBRACE("{"),
		/** "}" */
		RBRACE("}"),

		EPSILON(Grammar.EMPTY_STRING),
		/** End-of-file marker */
		EOF(Grammar.INPUT_ENDMARKER.getName());
		
		
		private final String terminalSymbol;
		
		private TokenType(String terminalSymbol) {
			this.terminalSymbol=terminalSymbol;
		}
		
		public String terminalSymbol(){
			return this.terminalSymbol;
		}
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
	 * E.g. for a Token of type BOOL this should return an instance of the
	 * Java-type Boolean
	 * 
	 * Same applies to the following TokenTypes:
	 * INT -> Integer
	 * REAL -> Float
	 * STRING -> String
	 * 
	 * @example Usage: If getType() == REAL, then (Float)getAttribute()
	 *          retrieves the value
	 * 
	 * @return Attribute value, may be null
	 */
	Object getAttribute();

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
