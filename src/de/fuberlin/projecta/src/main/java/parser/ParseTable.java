package parser;

public class ParseTable {

	private static final char DELIM = '#';

	private String[] nonTerminals, terminals;

	private String[][] table;

	public ParseTable(String[] nonTerminals, String[] terminals) {
		this.nonTerminals = nonTerminals;
		this.terminals = terminals;

		table = new String[nonTerminals.length][terminals.length];
	}

	public void setEntry(String nonT, String t, String entry) {
		for (int i = 0; i < nonTerminals.length; i++) {
			if (nonTerminals[i].equals(nonT)) {
				for (int j = 0; j < terminals.length; j++) {
					if (terminals[j].equals(t)) {
						if (table[i][j].equals("")) {
							table[i][j] = entry;
						} else {
							table[i][j] += DELIM + entry;
						}
					}
				}
			}
		}
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
	 */
	public String getEntry(String nonT, String t) {
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
