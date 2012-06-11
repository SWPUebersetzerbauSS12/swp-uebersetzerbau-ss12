package analysis.ast.nodes;

import analysis.SymbolTableStack;


public class Return extends Statement {
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		return "ret %" + ((AbstractSyntaxTree)getChild(0)).genCode();
	}
}
