package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;


public class IfElse extends Statement {
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		// semantics of if-else-statement is unambiguous
		// only need to check children
		for(int i = 0; i < this.getChildrenCount(); i++){
			if(!((AbstractSyntaxTree)this.getChild(i)).checkSemantics()){
				return false;
			}
		}
		return true;
	}

	@Override
	public String genCode() {
		// TODO Auto-generated method stub
		return null;
	}
}
