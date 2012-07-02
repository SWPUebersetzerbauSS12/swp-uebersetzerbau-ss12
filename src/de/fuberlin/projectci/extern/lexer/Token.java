package de.fuberlin.projectci.extern.lexer;

import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projectci.grammar.Grammar;




//import lombok.AllArgsConstructor;
//import lombok.Data;

//@AllArgsConstructor
public
//@Data
class Token implements IToken {

	private TokenType type;

	private Object attribute;

	private int lineNumber;
	private int offset;

	

	public Token(TokenType type, Object attribute, int lineNumber, int offset) {
		super();
		this.type = type;
		this.attribute = attribute;
		this.lineNumber = lineNumber;
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "<" + type + ", " + attribute + ", " + lineNumber + ", "
				+ offset + ">";
	}

	@Override
	public String getType() {
		return type!=null?type.toString():Grammar.EPSILON.toString();
	}

	@Override
	public Object getAttribute() {
		return attribute;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

}
