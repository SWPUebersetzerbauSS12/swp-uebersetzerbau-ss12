package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableHelper;


public class Id extends AbstractSyntaxTree {
	
	/**
	 * Should be set in genCode, when register is allocated
	 */
	
	private String value;
	
	public Id(String value){
		this.value = value;
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
	
	public Type getType(){
		return SymbolTableHelper.lookup(this.getValue(), this).getType();
	}
}
