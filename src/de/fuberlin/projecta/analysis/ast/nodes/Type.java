package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;

public class Type extends AbstractSyntaxTree {

	@Override
	public boolean checkSemantics() {
		return true;
	}
	
	public String genCode(){
		return "";
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}
