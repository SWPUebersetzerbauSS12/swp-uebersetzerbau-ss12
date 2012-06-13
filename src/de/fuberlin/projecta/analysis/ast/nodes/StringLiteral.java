package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;

public class StringLiteral extends Statement {
	
	private String value;
	
	public StringLiteral(String value) {
		this.value = value;
	}
	
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		return this.value + "\\00";
	}
	
	public String getValue() {
		return this.value;
	}
}
