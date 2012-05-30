package analysis.ast.nodes;

import parser.Symbol;
import parser.Tree;

public abstract class AbstractSyntaxTree extends Tree {

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

}
