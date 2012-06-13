package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;

public class Id extends AbstractSyntaxTree {
	
	private String value;
	
	public Id(String value){
		this.value = value;
	}
	
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		//can't have children!
		return true;
	}

	@Override
	public String genCode() {
		return value;
	}
	
	public String getValue(){
		return this.value;
	}
}
