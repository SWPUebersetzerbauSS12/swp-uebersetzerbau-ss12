package de.fuberlin.commons.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

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

	/** Basic type */
	BASIC("basic"),

	/** Boolean literal */
	BOOL_LITERAL("bool"),	
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

	/** End-of-file marker */
	EOF("eof");

	private static Map<String,TokenType> terminalSymbol2TokenType=new HashMap<String, TokenType>();

	static{
		for(TokenType t : EnumSet.allOf(TokenType.class))
			terminalSymbol2TokenType.put(t.terminalSymbol(), t);
	}



	private final String terminalSymbol;

	private TokenType(String terminalSymbol) {
		this.terminalSymbol=terminalSymbol;			
	}

	public String terminalSymbol(){
		return this.terminalSymbol;
	}

	public static TokenType byTerminalSymbol(String s){
		return terminalSymbol2TokenType.get(s);
	}

}
