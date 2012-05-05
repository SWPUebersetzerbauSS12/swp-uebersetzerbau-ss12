package parser.nodes;

import semantic.analysis.SymbolTableStack;

public class NoOpTree extends Tree {
	
	public NoOpTree() {
		super("NOOP");
	}

	@Override
	public void run(SymbolTableStack tables) {
	}
}
