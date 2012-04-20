package lexer;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class Token {

	/**
	 ****terminal symbols******
	 * RELOP - relational operators <(LT), <=(LE), ==(EQ), !=(NE), >(GT), >=(GE)   
	 * BOOLOP - ||(OR), &&(AND), !(NOT)
	 * ASSIGN - =
	 * ARITHOP - Arithmetic Operator +(SUM) -(SUB) *(MUL) /(DIV) -(NEG)
	 * BRL - (
	 * BRR - )
	 * SBRL - [
	 * SBRR - ]
	 * CBRL - {
	 * CBRR - }
	 * SEMIC - ;
	 * COMMA - ,
	 ***reserverd Words*****
	 * IF
	 * THEN
	 * ELSE
	 * WHILE
	 * DO
	 * BREAK
	 * RETURN
	 * PRINT 
	 * INT
	 * REAL
	 ****other*******
	 * ID - identifier
	 * STRING - String constant
	 * NUM - numeral constant
	 */
	public enum TYPE {
		ID, RELOP, BOOLOP, IF, THEN, ELSE, WHILE, DO, BREAK, 
		RETURN, PRINT, ASSIGN, ARITHOP, STRING, NUM,
		BRL, BRR, DEF, SBRL, SBRR, CBRL, CBRR, INT, REAL, SEMIC, COMMA

	}
	
	private TYPE name;
	
	private String attribute;
	
	@Override
	public String toString(){
		
		return "<" + name + ", " + attribute +">";
	}

}
