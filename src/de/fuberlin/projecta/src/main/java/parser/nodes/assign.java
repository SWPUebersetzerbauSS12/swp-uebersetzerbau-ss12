package parser.nodes;

import semantic.analysis.SymbolTableStack;


public class assign extends Tree {

	public assign(String name) {
		super(name);
	}

	@Override
	public void run(SymbolTableStack tables) {
		printTree();
		
	}
}
