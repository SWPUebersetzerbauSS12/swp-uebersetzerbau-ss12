package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SymbolTable;
import de.fuberlin.projecta.analysis.SymbolTableStack;
import de.fuberlin.projecta.parser.Symbol;
import de.fuberlin.projecta.parser.Tree;

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
	 **/
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	/**
	 * Code generation
	 **/

	public String genCode() {
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
	 * @return
	 */
	public String genStruct() {
		String out = "";
		for (int i = 0; i < getChildrenCount(); i++) {
			out += ((AbstractSyntaxTree) getChild(i)).genStruct();
		}
		return out;
	}

	public boolean checkTypes() {
		for (ISyntaxTree child : this.getChildren()) {
			if (!((AbstractSyntaxTree) child).checkTypes()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Reimplement in subclass if needed!
	 */
	public void checkSemantics() {
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
