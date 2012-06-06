package analysis.ast.nodes;

import analysis.SymbolTableStack;


public class Break extends Statement {
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}
}
