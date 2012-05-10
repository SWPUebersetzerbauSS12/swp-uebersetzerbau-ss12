package semantic.analysis;

import java.util.Stack;

public class SymbolTableStack {

	private static Stack<SymbolTable> stack;

	public SymbolTableStack() {
		// start with one empty symbolTable
		stack = new Stack<SymbolTable>();
		stack.push(new SymbolTable());
	}

	/**
	 * Searches one entry in all symbol tables, beginning from the top
	 * 
	 * @param name
	 * @return The object associated with name, null if none is found.
	 */
	public Object findEntry(String name) {
		for (int i = 0; i < stack.size(); ++i) {
			SymbolTable table = stack.get(i);
			Object content = table.lookup(name);
			if (content != null)
				return content;
		}
		return null;
	}

	/**
	 * Get the current SymbolTable on top of the stack
	 * 
	 * @return Symbol table
	 */
	public SymbolTable top() {
		return stack.peek();
	}

	/**
	 * Pop last added symbol table
	 * 
	 * @return The last pushed SymbolTable
	 */
	public SymbolTable pop() {
		return stack.pop();
	}

	/**
	 * Push a new symbol table on top of the stack
	 * 
	 * @param Table
	 */
	public void push() {
		stack.push(new SymbolTable());
	}

	/**
	 * @return Current size of the stack
	 */
	public int size() {
		return stack.size();
	}
}
