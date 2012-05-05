package semantic.analysis;

import java.util.Stack;

public class SymbolTableStack {

	private static Stack<SymbolTable> symbolTables;

	public SymbolTableStack() {
		// start with one empty symbolTable
		symbolTables = new Stack<SymbolTable>();
		symbolTables.push(new SymbolTable());
	}

	/**
	 * Attention: popped tables must be pushed again when done writing!
	 * 
	 * @return the last pushed SymbolTable
	 */
	public SymbolTable getLastSymbolTable() {
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
