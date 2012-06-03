package analysis.ast.nodes;

import parser.ISyntaxTree;
import analysis.SymbolTableStack;


public class Block extends Statement {
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
		for(ISyntaxTree a : this.getChildren()){
			if(!((AbstractSyntaxTree)a).checkSemantics())
				return false;
		}
		return true;
	}
}
