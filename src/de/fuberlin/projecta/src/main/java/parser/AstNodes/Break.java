package parser.AstNodes;

import parser.Symbol;
import semantic.analysis.SymbolTableStack;


public class Break extends Statement {

	public Break(Symbol symbol) {
		super(symbol);
	}

	public void run(SymbolTableStack tables) {

	}
}
