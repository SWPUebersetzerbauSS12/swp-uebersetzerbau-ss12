package analysis.ast.nodes;

import lombok.Getter;
import parser.ISyntaxTree;
import parser.Symbol;
import parser.Tree;
import analysis.SymbolTable;
import analysis.SymbolTableStack;

public abstract class AbstractSyntaxTree extends Tree {

	/**
	 * This is the symbolTable of this node. Caution: it may be null!
	 */
	@Getter
	SymbolTable table;

	public SymbolTable getHigherTable() {
		while (getParent() != null) {
			if (((AbstractSyntaxTree) getParent()).getTable() != null) {
				return ((AbstractSyntaxTree) getParent()).getTable();
			}
		}
		return null;
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
	public abstract void buildSymbolTable(SymbolTableStack tables);

	/**
	 * Code generation
	 **/

	public String genCode(){
		String out = "";
		for (int i=0; i < getChildrenCount();i++){
			out += ((AbstractSyntaxTree)getChild(i)).genCode() + "\n";
		}
		return out;
	}

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
