package parser;

import lexer.IToken.TokenType;

public class Symbol {
	
	private Object symbol;
	
	/**
	 * Construct from string
	 * 
	 * FIXME: This ctor should be removed. It's not type-safe
	 * @param string
	 */
	public Symbol(String string) {
		try {
			NonTerminal nonT = NonTerminal.valueOf(string);
			this.symbol = nonT;
		} catch (IllegalArgumentException e) {}

		if (this.symbol == null) {
			TokenType t = TokenType.valueOf(string);
			this.symbol = t;
		}
	}
	
	public Symbol(TokenType terminal) {
		this.symbol = terminal;
	}
	
	public Symbol(NonTerminal nonTerminal) {
		this.symbol = nonTerminal;
	}
	
	public TokenType asTerminal() { return (TokenType)symbol; }
	public NonTerminal asNonTerminal() { return (NonTerminal)symbol; }
	
	public boolean isTerminal() { return symbol instanceof TokenType; }
	public boolean isNonTerminal() { return symbol instanceof NonTerminal; }

}
