package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;

public class BoolLiteral extends Statement {
	
	private boolean value;
	
	public BoolLiteral(boolean value) {
		this.value = value;
	}
	
	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		return "i8 " + ((this.value)?"1":"0");
	}
	
	public boolean getValue(){
		return this.value;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}
