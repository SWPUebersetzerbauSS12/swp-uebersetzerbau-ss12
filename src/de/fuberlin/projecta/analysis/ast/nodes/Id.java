package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;

public class Id extends AbstractSyntaxTree {
	
	/**
	 * Should be set in genCode, when register is allocated
	 */
	private int register;
	
	@Getter
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

	public int getRegister() {
		return register;
	}

	public void setRegister(int register) {
		this.register = register;
	}
}
