package semantic.analysis;

import parser.ISyntaxTree;

public class SemanticAnalyser {

	private ISyntaxTree tree;
	private SymbolTableStack tables;

	public SemanticAnalyser(ISyntaxTree tree) {
		this.tree = tree;
		tables = new SymbolTableStack();
	}
	
	public void run(){
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
