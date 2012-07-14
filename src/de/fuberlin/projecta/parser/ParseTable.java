package de.fuberlin.projecta.parser;

import java.util.HashMap;

import de.fuberlin.commons.lexer.TokenType;

public class ParseTable {

	private static final char DELIM = '#';

	private boolean isAmbigous = false;

	private HashMap<String, String> table = new HashMap<String,String>();
	
	private static String toKey(NonTerminal nonT, TokenType t) {
		return nonT.toString() + "/" + t.toString();
	}

	/**
	 * Stores the BNF String into the right cell, only if terminals and
	 * nonTerminals are unique! (Otherwise it takes the first occurrence).
	 *
	 * @throws IllegalStateException
	 */
	public void setEntry(NonTerminal nonT, TokenType t, String entry)
			throws IllegalStateException {

		final String key = toKey(nonT, t);
		
		// check if combination is already in parse table
		if (table.containsKey(key)) {
			String oldEntry = table.get(key);
			table.put(key, oldEntry + DELIM + entry);
			isAmbigous = true;
			throw new IllegalStateException(
					"parsing table is ambigous in cell ["
							+ nonT + "," + t + "]");
		}

		table.put(key, entry);
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
		final String key = toKey(nonT, t);
		return table.get(key);
	}

	public boolean isAmbigous() {
		return isAmbigous;
	}

}
