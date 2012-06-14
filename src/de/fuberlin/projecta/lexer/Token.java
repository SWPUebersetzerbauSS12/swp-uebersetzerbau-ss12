package de.fuberlin.projecta.lexer;

import java.util.HashMap;

import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.commons.lexer.TokenType;

public class Token implements IToken {

	/**
	 * Get the real type of this token (used internally only)
	 */
	private TokenType internalType;

	private Object attribute;

	private int lineNumber;
	
	private int offset;

	private HashMap<TokenType, String> mapping = new HashMap<TokenType, String>();

	public Token(TokenType internalType, Object attribute, int lineNumber,
			int offset) {
		this.internalType = internalType;
		this.attribute = attribute;
		this.lineNumber = lineNumber;
		this.offset = offset;

		initMapping();
	}

	@Override
	public String getText() {
		return mapping.get(internalType);
	}

	@Override
	public String getType() {
		return internalType.toString();
	}

	@Override
	public String toString() {
		return "<" + internalType + ", " + attribute + ", " + lineNumber + ", "
				+ offset + ">";
	}

	private void initMapping() {
		mapping.put(TokenType.OP_LE, "<");
		mapping.put(TokenType.OP_LE, "<=");
		mapping.put(TokenType.OP_EQ, "==");
		mapping.put(TokenType.OP_NE, "!=");
		mapping.put(TokenType.OP_GT, ">");
		mapping.put(TokenType.OP_GE, ">=");
		mapping.put(TokenType.OP_OR, "||");
		mapping.put(TokenType.OP_AND, "&&");
		mapping.put(TokenType.OP_NOT, "!");
		mapping.put(TokenType.OP_ADD, "+");
		mapping.put(TokenType.OP_MINUS, "-");
		mapping.put(TokenType.OP_MUL, "*");
		mapping.put(TokenType.OP_DIV, "/");
		mapping.put(TokenType.OP_ASSIGN, "=");
		mapping.put(TokenType.OP_COMMA, ",");
		mapping.put(TokenType.OP_DOT, ".");
		mapping.put(TokenType.OP_SEMIC, ";");
		mapping.put(TokenType.IF, "if");
		mapping.put(TokenType.THEN, "then");
		mapping.put(TokenType.ELSE, "else");
		mapping.put(TokenType.WHILE, "while");
		mapping.put(TokenType.DO, "do");
		mapping.put(TokenType.BREAK, "break");
		mapping.put(TokenType.RETURN, "return");
		mapping.put(TokenType.PRINT, "print");
		mapping.put(TokenType.DEF, "def");
		mapping.put(TokenType.BASIC, "basic");
		mapping.put(TokenType.RECORD, "record");
		mapping.put(TokenType.ID, "id");
		mapping.put(TokenType.STRING_LITERAL, "string");
		mapping.put(TokenType.INT_LITERAL, "num");
		mapping.put(TokenType.LPAREN, "(");
		mapping.put(TokenType.RPAREN, ")");
		mapping.put(TokenType.LBRACKET, "[");
		mapping.put(TokenType.RBRACKET, "]");
		mapping.put(TokenType.LBRACE, "{");
		mapping.put(TokenType.RBRACE, "}");
		mapping.put(TokenType.EOF, "eof");
	}

	public TokenType getInternalType() {
		return internalType;
	}

	public Object getAttribute() {
		return attribute;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Token token = (Token) o;

		if (lineNumber != token.lineNumber) return false;
		if (offset != token.offset) return false;
		if (attribute != null ? !attribute.equals(token.attribute) : token.attribute != null) return false;
		return internalType == token.internalType;

	}

	@Override
	public int hashCode() {
		int result = internalType != null ? internalType.hashCode() : 0;
		result = 31 * result + (attribute != null ? attribute.hashCode() : 0);
		result = 31 * result + lineNumber;
		result = 31 * result + offset;
		return result;
	}
}
