package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTable;
import de.fuberlin.projecta.analysis.SymbolTableStack;
import de.fuberlin.projecta.parser.Symbol;
import de.fuberlin.projecta.parser.Tree;

/**
 * This is the superclass of *all* AST nodes
 * 
 * It provides the necessary interfaces for all subclasses
 */
public abstract class AbstractSyntaxTree extends Tree {

	/**
	 * This is the symbolTable of this node. Caution: it may be null!
	 */
	SymbolTable table;

	public SymbolTable getTable() {
		return table;
	}

	public SymbolTable getHigherTable() {
		if (getParent() != null) {
			AbstractSyntaxTree parent = (AbstractSyntaxTree) getParent();
			while (parent != null) {
				if (((AbstractSyntaxTree) getParent()).getTable() != null) {
					return ((AbstractSyntaxTree) getParent()).getTable();
				}
				parent = (AbstractSyntaxTree) parent.getParent();
			}
		}
		return null;
	}

	/**
	 * Every block in LLVM has it's own memory counter, so does ours.
	 * 
	 * @return
	 * 		The highest node above this, which is instance of Block.
	 */
	public Block getHighestBlock() {
		Block block = null;
		if (getParent() != null) {
			ISyntaxTree parent = getParent();
			while (parent != null) {
				if (parent instanceof Block) {
					block = (Block) parent;
				}
				parent = parent.getParent();
			}
		}
		return block;
	}

	public AbstractSyntaxTree() {
		super(null);
	}

	public AbstractSyntaxTree(Symbol symbol) {
		super(symbol);
	}

	/**
	 * Method for building the SymbolTables that the nodes should implement
	 * 
	 * Default implementation does nothing
	 * @note Reimplement in subclass if needed!
	 **/
	public void buildSymbolTable(SymbolTableStack tables) {}

	/**
	 * Code generation
	 * 
	 * Default implementation recursively calls this on its children
	 **/
	public String genCode() throws SemanticException {
		String out = "";
		for (int i = 0; i < getChildrenCount(); i++) {
			out += ((AbstractSyntaxTree) getChild(i)).genCode() + "\n";
		}
		return out;
	}

	/**
	 * We must explicitly go through the whole tree twice, since identified
	 * records has to be placed in the head!
	 * 
	 * Default implementation recursively calls this on its children
	 * @return Generated structs (LLVM code)
	 */
	protected String genStruct() throws SemanticException {
		String out = "";
		for (int i = 0; i < getChildrenCount(); i++) {
			out += ((AbstractSyntaxTree) getChild(i)).genStruct();
		}
		return out;
	}

	/**
	 * Check types
	 * 
	 * Default implementation recursively checks types
	 * @note Reimplement in subclass if needed!
	 */
	public void checkTypes() throws SemanticException {
		for (ISyntaxTree child : this.getChildren()) {
			((AbstractSyntaxTree) child).checkTypes();
		}
	}

	/**
	 * Check semantics (other than type errors)
	 * 
	 * Default implementation recursively checks semantics
	 * @note Reimplement in subclass if needed!
	 */
	public void checkSemantics() throws SemanticException {
		for (int i = 0; i < this.getChildrenCount(); i++) {
			((AbstractSyntaxTree) this.getChild(i)).checkSemantics();
		}
	}

	@Override
	public String toString() {
		// remove prefix from class name
		final String className = this.getClass().getName()
				.replaceAll("^.*\\.", "");

		String value = "";
		if (this instanceof BinaryOp) {
			value += ((BinaryOp) this).getOp();
		} else if (this instanceof IntLiteral) {
			value += ((IntLiteral) this).getValue();
		} else if (this instanceof Id) {
			value += ((Id) this).getValue();
		} else if (this instanceof UnaryOp) {
			value += ((UnaryOp) this).getOp();
		} else if (this instanceof BasicType) {
			value += ((BasicType) this).getTokenType();
		}

		if (getTable() != null) {
			value += "table contents: " + getTable();
		}

		if (value.isEmpty())
			return className;
		return className + " [" + value + "]";
	}

}
