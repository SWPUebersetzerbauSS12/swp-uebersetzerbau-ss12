package analysis.ast.nodes;

import analysis.SymbolTableStack;


public class Block extends Statement {
	@Override
	public void buildSymbolTable(SymbolTableStack stack){
		stack.push();
		for(int i = 0; i < this.getChildrenCount(); i++){
			((AbstractSyntaxTree)(this.getChild(i))).buildSymbolTable(stack);
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

	// using super implementation
}
