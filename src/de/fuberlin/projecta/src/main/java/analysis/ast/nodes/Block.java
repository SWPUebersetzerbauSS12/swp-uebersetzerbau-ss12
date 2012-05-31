package analysis.ast.nodes;

import analysis.SymbolTableStack;


public class Block extends Statement {
	public void run(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		for(AbstractSyntaxTree a : ((AbstractSyntaxTree[])this.getChildren().toArray())){
			if(!a.checkSemantics())
				return false;
		}
		return true;
	}
}
