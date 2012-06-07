package analysis.ast.nodes;

import analysis.SymbolTableStack;


public class While extends Statement {
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		for(int i = 0; i < this.getChildrenCount(); i++){
			if(!((AbstractSyntaxTree)getChild(i)).checkSemantics()){
				return false;
			}
		}
		return true;
	}
}
