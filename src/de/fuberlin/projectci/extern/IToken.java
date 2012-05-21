package de.fuberlin.projectci.extern;

import de.fuberlin.projectci.grammar.Grammar;

public interface IToken {

	/**
	 * TokenType enum für Ausdrucksgrammatik
	 * TODO TokenType als Enum macht für einen Parsergenarator wenig Sinn (es sei denn man will für jede Grammatik die TokenTypes neu generieren)
	 * TODO Muss noch mit den anderen Gruppen abgestimmt werden
	 */
	public enum TokenType {
		OP_ADD("+"),  
		OP_MUL ("*"),
		ID ("id"),
		LPAREN("("),
		RPAREN(")"),
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
