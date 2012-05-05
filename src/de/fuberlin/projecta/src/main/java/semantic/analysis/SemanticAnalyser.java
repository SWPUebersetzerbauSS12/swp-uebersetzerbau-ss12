package semantic.analysis;

import parser.ISyntaxTree;

public class SemanticAnalyser {

	private ISyntaxTree tree;
	private SymbolTableStack tables;

	public SemanticAnalyser(ISyntaxTree tree) {
		this.tree = tree;
		tables = new SymbolTableStack();
	}

	/**
	 * Runs every nodes run method in depth-first-left-to-right order.
	 * 
	 * @param tree
	 */
	public void parseTreeForSemanticActions(ISyntaxTree tree) {
		for (int i = 0; i < tree.getChildrenCount(); i++) {
			parseTreeForSemanticActions(tree.getChild(i));
		}
		tree.run();
	}

	/**
	 * Checks whether the tree contains disallowed semantic.
	 */
	public void parseTreeForRemoval() {
		// TODO: think about how to smartly encode disallowed trees
	}
}
