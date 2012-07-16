package de.fuberlin.projecta.analysis.ast;


public class RealLiteral extends Literal {
	
	private double value;
	
	public RealLiteral(double value){
		this.value = value;
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
		return BasicType.TYPE_REAL_STRING;
	}
}
