package analysis.ast.nodes;

import parser.ISyntaxTree;
import analysis.SymbolTableStack;


public class Program extends AbstractSyntaxTree {
	public void run(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		for(ISyntaxTree tree : this.getChildren()){
			AbstractSyntaxTree child = (AbstractSyntaxTree) tree;
			if (!child.checkSemantics()){
				return false;
			}
		}
		return true;
	}
}
