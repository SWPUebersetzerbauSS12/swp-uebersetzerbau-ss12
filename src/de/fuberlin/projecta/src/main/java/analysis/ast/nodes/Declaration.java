package analysis.ast.nodes;

import analysis.SymbolTableStack;

public class Declaration extends AbstractSyntaxTree {
	@Override
	public void buildSymbolTable(SymbolTableStack tables) {
		tables.top().insertEntry((Id)getChild(1), (Type)getChild(0));
	}

	@Override
	public boolean checkSemantics() {
		// TODO Auto-generated method stub
		return false;
	}
}
