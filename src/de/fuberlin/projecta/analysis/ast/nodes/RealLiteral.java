package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;

public class RealLiteral extends Statement {
	
	private double value;
	
	public RealLiteral(double value){
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
		return "double " + this.value;
	}
	
	public double getValue(){
		return this.value;
	}
}
