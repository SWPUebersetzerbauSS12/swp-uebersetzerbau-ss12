package analysis.ast.nodes;

import lombok.Getter;
import parser.ISyntaxTree;
import parser.Symbol;
import parser.Tree;
import analysis.SymbolTable;

public abstract class AbstractSyntaxTree extends Tree {

	/**
	 * This is the symbolTable of this node. Caution: it may be null!
	 */
	@Getter
	SymbolTable table;

	public AbstractSyntaxTree() {
		super();
	}

	public AbstractSyntaxTree(Symbol symbol) {
		super(symbol);
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
		if (object.getClass() == this.getClass()) {
			ISyntaxTree ot = (ISyntaxTree) object;
			if (getChildrenCount() == ot.getChildrenCount()) {
				for (int i = 0; i < getChildrenCount(); i++) {
					if (!getChild(i).equals(ot.getChild(i))) {
						break;
					} 					
				}
				return true;
			}
		}
		return false;
	}
}
