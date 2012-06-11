package analysis.ast.nodes;

import analysis.SymbolTableStack;

public class Type extends AbstractSyntaxTree {
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}
	
	public String genCode(){
		return null;
	}
}
