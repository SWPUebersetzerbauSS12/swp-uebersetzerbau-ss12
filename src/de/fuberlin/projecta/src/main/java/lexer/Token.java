package lexer;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public
@Data
class Token {

	public enum TYPE {
		/** relational operators <(LT), <=(LE), ==(EQ), !=(NE), >(GT), >=(GE) */
		RELOP,
		/** ||(OR), &&(AND), !(NOT) */
		BOOLOP,
		/** Arithmetic Operator +(SUM) -(SUB) *(MUL) /(DIV) -(NEG) */
		ARITHOP,
		/** Assignment operator */
		ASSIGN,
		/** Other reserverd words */
		IF, THEN, ELSE,
		WHILE, DO, BREAK, // no continue?
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
		/**
		 * For array defintions, this marks the field count
		 */
		NUM,
		/** Terminal symbols */
		BRL, BRR, SBRL, SBRR, CBRL, CBRR,
		/** Comma ',' operator */
		COMMA,
		/** Semicolon (;) operator */
		SEMIC
	}

	private TYPE name;

	private String attribute;

	@Override
	public String toString() {
		return "<" + name + ", " + attribute + ">";
	}

}
