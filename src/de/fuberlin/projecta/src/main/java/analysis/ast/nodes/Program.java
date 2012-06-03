package analysis.ast.nodes;

import parser.ISyntaxTree;
import analysis.SymbolTableStack;


public class Program extends AbstractSyntaxTree {
	
	@Override
	public void buildSymbolTable(SymbolTableStack stack){
		stack.push();
		for(ISyntaxTree child : this.getChildren()){
			child.buildSymbolTable(stack);
		}
		table = stack.pop();	
		
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
