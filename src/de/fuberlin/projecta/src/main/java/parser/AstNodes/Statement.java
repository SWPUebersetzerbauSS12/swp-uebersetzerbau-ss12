package parser.AstNodes;

import parser.Symbol;
import parser.Tree;
import semantic.analysis.SymbolTableStack;


public abstract class Statement extends Tree {

	public Statement(Symbol symbol) {
		super(symbol);
	}

	public void run(SymbolTableStack tables) {

	}
}
