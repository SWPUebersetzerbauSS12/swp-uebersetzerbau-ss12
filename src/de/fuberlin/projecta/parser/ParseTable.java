package de.fuberlin.projecta.parser;

import java.util.HashMap;

import de.fuberlin.commons.lexer.TokenType;

public class ParseTable {

	private static final char DELIM = '#';

	private boolean isAmbigous = false;

	private String[][] table 
		= new String[NonTerminal.values().length][TokenType.values().length];

	/**
	 * Stores the BNF String into the right cell, only if terminals and
	 * nonTerminals are unique! (Otherwise it takes the first occurrence).
	 *
	 * @throws IllegalStateException
	 */
	public void setEntry(NonTerminal nonT, TokenType t, String entry)
			throws IllegalStateException {

		String oldEntry = table[nonT.ordinal()][t.ordinal()];
		// check if combination is already in parse table
		if (oldEntry != null) {
			table[nonT.ordinal()][t.ordinal()] += DELIM + entry;
			isAmbigous = true;
			throw new IllegalStateException(
					"parsing table is ambigous in cell ["
							+ nonT + "," + t + "]");
		}

		table[nonT.ordinal()][t.ordinal()] = entry;
	}

	/**
	 * Get an entry out of the parse table
	 * 
	 * @param nonT
	 *            non terminal
	 * @param t
	 *            terminal
	 * @return All entries of the parsing table cell with the given arguments
	 *         nonT and t. Entries are separated by one delimiter. If no entry
	 *         is found null is returned.
	 */
	public String getEntry(NonTerminal nonT, TokenType t) {
		String entry = table[nonT.ordinal()][t.ordinal()];
		return entry;
	}

	public HashMap<TokenType,String> getEntries(NonTerminal nonT) {
		HashMap<TokenType,String> entries = new HashMap<TokenType,String>();
		for (TokenType t : TokenType.values()) {
			String entry = table[nonT.ordinal()][t.ordinal()];
			if (entry != null)
				entries.put(t, entry);
		}
		return entries;
	}

	public boolean isAmbigous() {
		return isAmbigous;
	}

}
