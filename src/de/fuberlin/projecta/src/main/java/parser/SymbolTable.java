package parser;

import java.util.Stack;

/**
 * @author Christian Cikryt
 */
public class SymbolTable implements SymbolTableEntry {
	private Stack<SymbolTableEntry> list = new Stack<SymbolTableEntry>();

	public SymbolTableEntry getEntryByName(String name) {
		return null;
	}
}
