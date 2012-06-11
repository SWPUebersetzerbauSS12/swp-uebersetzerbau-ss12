package analysis;

import java.util.List;
import java.util.Stack;

public class SymbolTableStack {

	private Stack<SymbolTable> stack = new Stack<SymbolTable>();

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
	
	public boolean isEmpty(){
		return stack.isEmpty();
	}

	@Override
	public String toString(){
		return stack.toString();
	}
}
