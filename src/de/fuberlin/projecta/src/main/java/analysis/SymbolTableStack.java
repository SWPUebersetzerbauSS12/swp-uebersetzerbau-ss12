package analysis;

import java.util.List;
import java.util.Stack;

public class SymbolTableStack {

	private Stack<SymbolTable> stack = new Stack<SymbolTable>();

	/**
	 * Searches one entry in all symbol tables, beginning from the top
	 * 
	 * @param name
	 * @return The object associated with name, null if none is found.
	 */
	public EntryType findEntry(String name) {
		for (int i = stack.size() -1; i >= 0; --i) {
			SymbolTable table = stack.get(i);
			EntryType content = table.lookup(name);
			if (content != null)
				return content;
		}
		return null;
	}
	
	/**
	 * @return	A set of entries of the current top symbolTable
	 */
	public List<EntryType> getEntries(){
		return top().getEntries();
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
