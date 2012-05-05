package semantic.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SymbolTableStack {

	private static Stack<SymbolTable> symbolTables;

	public SymbolTableStack() {
		// start with one empty symbolTable
		symbolTables = new Stack<SymbolTable>();
		symbolTables.push(new SymbolTable());
	}

	/**
	 * Searches one entry in all SymbolTables, with respect to the stack order.
	 * After finding it rearranges the table stack to the previous order.
	 * 
	 * @param name
	 * @return the object associated with name, null if none is found.
	 */
	public Object findEntry(String name) {
		List<SymbolTable> tables = new ArrayList<SymbolTable>();
		SymbolTable table;

		do {
			table = symbolTables.pop();
			if (table != null)
				tables.add(table);
			if (table.lookup(name) != null) {
				for (int i = tables.size() - 1; i >= 0; i--) {
					pushSymbolTable(tables.get(i));
				}
				return table.lookup(name);
			}
		} while (table != null);

		for (int i = tables.size() - 1; i >= 0; i--) {
			pushSymbolTable(tables.get(i));
		}
		return null;
	}

	/**
	 * Searches one entry in all SymbolTables, with respect to the stack order.
	 * After finding and updating the content, it rearranges the table stack to
	 * the previous order.
	 * 
	 * @param name
	 *            the name to search for
	 * @param content
	 *            the content to override previous content
	 */
	public void insertEntry(String name, Object content) {
		List<SymbolTable> tables = new ArrayList<SymbolTable>();
		SymbolTable table;

		do {
			table = symbolTables.pop();
			if (table != null)
				tables.add(table);
			if (table.lookup(name) != null) {
				table.insertEntry(name, content);
				break;
			}
		} while (table != null);

		for (int i = tables.size() - 1; i >= 0; i--) {
			pushSymbolTable(tables.get(i));
		}
	}
	
	/**
	 * Attention: popped tables must be pushed again when done writing!
	 * 
	 * @return the last pushed SymbolTable
	 */
	public SymbolTable popSymbolTable() {
		return symbolTables.pop();
	}

	/**
	 * Adds the given table onto the symbolTable stack
	 * 
	 * @param table
	 */
	public void pushSymbolTable(SymbolTable table) {
		symbolTables.push(table);
	}
}
