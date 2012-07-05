package de.fuberlin.projecta.parser;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISymbol;

public class Symbol implements ISymbol {

	/**
	 * Internal symbols, for building up the parse tree
	 */
	public enum Reserved {
		EPSILON("ε"), // epsilon production
		SP("_SP"); // stack pointer
		
		private static Map<String,Reserved> terminalSymbol2Reserved
			= new HashMap<String, Reserved>();
		
		static{
			for(Reserved t : EnumSet.allOf(Reserved.class)){
				terminalSymbol2Reserved.put(t.getTerminalSymbol(), t);
			}
		}
		
		private final String terminalSymbol;
		
		private Reserved(String terminalSymbol) {
			this.terminalSymbol=terminalSymbol;			
		}
		
		public String getTerminalSymbol(){
			return this.terminalSymbol;
		}

		public static Reserved byTerminalSymbol(String s){
			return terminalSymbol2Reserved.get(s);
		}
	}

	private Object symbol;

	/**
	 * Constructor for reserved (internal) symbols
	 */
	public Symbol(Reserved symbol) {
		this.symbol = symbol;
	}

	/**
	 * Construct Symbol instance from string
	 * 
	 * @param string A string describing a terminal or non-terminal or reserved symbol 
	 */
	public Symbol(String string) {
		// parse by definitions (for the generic parser groups)
		this.symbol=Reserved.byTerminalSymbol(string);
		if (symbol!=null)
			return;
		this.symbol = NonTerminal.byNonTerminalSymbol(string);
		if (symbol!=null)
			return;
		this.symbol = TokenType.byTerminalSymbol(string);
		if (symbol!=null)
			return;

		// parse by enum value (for projecta)
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
		try {
			this.symbol = TokenType.valueOf(string);
			return;
		} catch (IllegalArgumentException e) {
		}
		
		if (symbol == null)
			throw new RuntimeException("Failed to resolve symbol: " + string);
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

	@Override
	public String getName() {
		if (isTerminal())
			return asTerminal().toString();
		if (isNonTerminal())
			return asNonTerminal().toString();
		if (isReservedTerminal()) {
			return asReservedTerminal().toString();
		}
		// never reached
		return "invalid";
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;

		if (other instanceof Symbol) {
			Symbol that = (Symbol)other;
			if (this.getName().equals(that.getName()))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

}
