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
		if(getParent() != null){
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
	
	public Block getHighestBlock(){
		Block block = null;
		if(getParent() != null){
			ISyntaxTree parent = getParent();
			while(parent != null){
				if(parent instanceof Block){
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
	public void buildSymbolTable(SymbolTableStack tables){
		
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
	
	public abstract boolean checkTypes();

	/**
	 * 
	 * @return <code>true</code> if semantic is well in this node and in
	 *         (recursively) <b><u>all</u></b> child nodes
	 */
	public abstract boolean checkSemantics();

	/**
	 * An ast node equals one another ast node if it has the same type, the same
	 * amount of children and all children must equal the corresponding child in
	 * the node to check on equality.
	 * 
	 * TODO: Can someone please check this to work properly?
	 */
	@Override
	public boolean equals(Object object) {
		if (object != null) {
			if (object.getClass() == this.getClass()) {
				if (this instanceof BasicType) {
					if (((BasicType) this).getType() != ((BasicType) object)
							.getType()) {
						return false;
					}
				}
				ISyntaxTree ot = (ISyntaxTree) object;
				if (getChildrenCount() == ot.getChildrenCount()) {
					for (int i = 0; i < getChildrenCount(); i++) {
						if (!getChild(i).equals(ot.getChild(i))) {
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}
}
