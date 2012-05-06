package parser;

import lexer.IToken.TokenType;

public class Symbol {

	public enum Reserved {
		EPSILON, // epsilon production
		SP, // stack pointer
	}

	private Object symbol;

	/**
	 * Constructor for reserved (internal) symbols
	 */
	public Symbol(Reserved symbol) {
		this.symbol = symbol;
	}

	/**
	 * Construct from string
	 * 
	 * FIXME: This ctor should be removed. It's not type-safe
	 * 
	 * @param string
	 */
	public Symbol(String string) {
		try {
			Reserved reserved = Reserved.valueOf(string);
			this.symbol = reserved;
			return;
		} catch (IllegalArgumentException e) {
		}
		try {
			NonTerminal nonT = NonTerminal.valueOf(string);
			this.symbol = nonT;
			return;
		} catch (IllegalArgumentException e) {
		}

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

	public TokenType asTerminal() {
		return (TokenType) symbol;
	}

	public NonTerminal asNonTerminal() {
		return (NonTerminal) symbol;
	}

	public Reserved asReservedTerminal() {
		return (Reserved) symbol;
	}

	public boolean isTerminal() {
		return symbol instanceof TokenType;
	}

	public boolean isNonTerminal() {
		return symbol instanceof NonTerminal;
	}

	public boolean isReservedTerminal() {
		return symbol instanceof Reserved;
	}

	@Override
	public String toString() {
		if (isTerminal())
			return "<T," + asTerminal() + ">";
		if (isNonTerminal())
			return "<NT," + asNonTerminal() + ">";
		if (isReservedTerminal()) {
			return "<R," + asReservedTerminal() + ">";
		}
		// never reached
		return "<invalid>";
	}

}
