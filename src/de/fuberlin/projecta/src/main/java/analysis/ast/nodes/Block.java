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
		for(int i = 0; i < this.getChildrenCount(); i++){
			if(!((AbstractSyntaxTree)this.getChild(i)).checkSemantics()){
				return false;
			}
		}
		return true;
	}
}
