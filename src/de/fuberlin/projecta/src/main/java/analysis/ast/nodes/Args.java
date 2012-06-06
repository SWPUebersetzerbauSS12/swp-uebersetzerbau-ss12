package analysis.ast.nodes;

import analysis.SymbolTableStack;

public class Args extends AbstractSyntaxTree {
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}
}
