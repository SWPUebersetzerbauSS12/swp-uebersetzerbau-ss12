package parser;

import lexer.IToken.TokenType;
import lombok.Getter;

public class ParseTable {

	private static final char DELIM = '#';

	@Getter
	private boolean isAmbigous;

	private NonTerminal[] nonTerminals;
	private TokenType[] terminals;

	private String[][] table;

	public ParseTable(NonTerminal[] nonTerminals, TokenType[] terminals) {
		this.nonTerminals = nonTerminals;
		this.terminals = terminals;

		isAmbigous = false;

		table = new String[nonTerminals.length][terminals.length];
		for (int i = 0; i < nonTerminals.length; i++) {
			for (int j = 0; j < terminals.length; j++) {
				table[i][j] = "";
			}
		}
	}

	/**
	 * Stores the BNF String into the right cell, only if terminals and
	 * nonTerminals are unique! (Otherwise it takes the first occurrence).
	 * 
	 * @param nonT
	 * @param t
	 * @param entry
	 * @throws ParserException
	 */
	public void setEntry(NonTerminal nonT, TokenType t, String entry)
			throws ParserException {
		boolean found = false;
		for (int i = 0; i < nonTerminals.length; i++) {
			if (nonTerminals[i].equals(nonT)) {
				for (int j = 0; j < terminals.length; j++) {
					if (terminals[j].equals(t)) {
						if (table[i][j].equals("")) {
							table[i][j] = entry;
							found = true;
						} else {
							table[i][j] += DELIM + entry;
							isAmbigous = true;
							throw new ParserException(
									"parsing table is ambigous in cell ["
											+ nonT + "," + t + "]");
						}
					}
				}
			}
		}
		if (!found)
			throw new ParserException("Missing parsing table field [" + nonT
					+ "," + t + "]");
	}

	/**
	 * 
	 * 
	 * @param nonT
	 *            non terminal
	 * @param t
	 *            terminal
	 * @return All entries of the parsing table cell with the given arguments
	 *         nonT and t. Entries are separated by one delimiter. If no entry
	 *         is found null is returned.
	 * @throws ParserException 
	 */
	public String getEntry(NonTerminal nonT, TokenType t) throws ParserException {
		for (int i = 0; i < nonTerminals.length; i++) {
			if (nonTerminals[i].equals(nonT)) {
				for (int j = 0; j < terminals.length; j++) {
					if (terminals[j].equals(t)) {
						return table[i][j];
					}
				}
			}
		}
		return null;
	}

}
