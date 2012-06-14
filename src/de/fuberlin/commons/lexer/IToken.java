package de.fuberlin.commons.lexer;

public interface IToken {

	/**
	 * Get the type of this Token
	 * 
	 * @return Token type
	 */
	String getType();

	/**
	 * Return the textual representation of this token
	 *
	 * @return Textual representation
	 */
	String getText();

	/**
	 * Get the Token attribute value
	 * 
	 * E.g. for a Token of type BOOL this should return an instance of the
	 * Java-type Boolean
	 * 
	 * Same applies to the following TokenTypes:
	 * INT -> Integer
	 * REAL -> Double
	 * STRING -> String
	 * 
	 * Usage: If getType() == REAL, then (Double)getAttribute()
	 *        retrieves the value
	 * 
	 * @return Attribute value, may be null
	 */
	Object getAttribute();

	/**
	 * Get the start offset of this Token's attribute
	 * 
	 * The position is relative to the beginning of the line
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
