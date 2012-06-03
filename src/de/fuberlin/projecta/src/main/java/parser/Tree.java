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
	public void buildSymbolTable(SymbolTableStack tables) {
		// empty, this should be filled by AST nodes
	}

}
