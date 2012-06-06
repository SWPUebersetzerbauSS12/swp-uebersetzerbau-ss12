package analysis.ast.nodes;

import analysis.SymbolTableStack;


public class Return extends Statement {
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}
}
