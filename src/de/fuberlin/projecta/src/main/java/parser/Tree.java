package parser;

import analysis.SymbolTableStack;

public class Tree extends ITree {
	
	public Tree(){
		super(null);
	}

	public Tree(Symbol symbol) {
		super(symbol);
	}

	@Override
	public void run(SymbolTableStack tables) {
	}

}
