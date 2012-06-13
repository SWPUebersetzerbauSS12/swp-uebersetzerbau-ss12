package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;


public class Program extends AbstractSyntaxTree {
	
	@Override
	public void buildSymbolTable(SymbolTableStack stack){
		stack.push();
		for(int i = 0; i < this.getChildrenCount(); i++){
			((AbstractSyntaxTree)this.getChild(i)).buildSymbolTable(stack);
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
	
	/*
	 * GenCode already implemented by AbstractSyntaxTree
	 */
}
