package parser.nodes;

import semantic.analysis.SymbolTableStack;

public class NoOpTree extends Tree {
	
	public NoOpTree(String name) {
		super(name);
	}

	@Override
	public void run(SymbolTableStack tables) {
	}
}
