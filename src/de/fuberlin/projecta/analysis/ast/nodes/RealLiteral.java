package de.fuberlin.projecta.analysis.ast.nodes;


public class RealLiteral extends Type {
	
	private double value;
	
	public RealLiteral(double value){
		this.value = value;
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
	
	@Override
	public String toTypeString(){
		return TYPE_REAL_STRING;
	}
}