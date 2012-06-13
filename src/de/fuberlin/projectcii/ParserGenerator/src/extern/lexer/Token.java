package de.fuberlin.projectcii.ParserGenerator.src.extern.lexer;

import de.fuberlin.commons.lexer.IToken;


/*import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;*/

//@AllArgsConstructor
//@Data
public class Token implements IToken {

	/**
	 * Get the real type of this token (used internally only)
	 */
	//@Getter
	private TokenType internalType;

	private Object attribute;

	private int lineNumber;
	private int offset;
	
	public Token(TokenType type, Object attribute, int lineNumber, int offset) {
		super();
		this.internalType = type;
		this.attribute = attribute;
		this.lineNumber = lineNumber;
		this.offset = offset;
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

	@Override
	public Object getAttribute() {
		// TODO Auto-generated method stub
		return attribute;
	}

	@Override
	public int getOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLineNumber() {
		// TODO Auto-generated method stub
		return lineNumber;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

}
