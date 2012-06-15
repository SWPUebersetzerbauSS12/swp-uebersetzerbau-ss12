package de.fuberlin.projecta.parser;

import de.fuberlin.commons.lexer.TokenType;

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
			this.symbol = Reserved.valueOf(string);
			return;
		} catch (IllegalArgumentException e) {
		}
		try {
			this.symbol = NonTerminal.valueOf(string);
			return;
		} catch (IllegalArgumentException e) {
		}

		if (this.symbol == null) {
			this.symbol = TokenType.valueOf(string);
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
