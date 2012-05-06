package semantic.analysis;

import parser.ISyntaxTree;

public class SemanticAnalyzer {

	private ISyntaxTree tree;
	private SymbolTableStack tables;

	public SemanticAnalyzer(ISyntaxTree tree) {
		this.tree = tree;
		tables = new SymbolTableStack();
	}
	
	public void analyze() throws SemanticException {
		parseTreeForSemanticActions(tree);
		parseTreeForRemoval();
	}

	/**
	 * Runs every nodes run method in depth-first-left-to-right order.
	 * 
	 * @param tree
	 */
	private void parseTreeForSemanticActions(ISyntaxTree tree) {
		for (int i = 0; i < tree.getChildrenCount(); i++) {
			parseTreeForSemanticActions(tree.getChild(i));
		}

		tree.run(tables);
	}

	/**
	 * Checks whether the tree contains disallowed semantic.
	 */
	private void parseTreeForRemoval() {
		// TODO: think about how to smartly encode disallowed trees
	}
}
