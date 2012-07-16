package de.fuberlin.projecta.analysis.ast;


public class BoolLiteral extends Literal {
	
	private boolean value;
	
	public BoolLiteral(boolean value) {
		this.value = value;
	}

	@Override
	public String genCode() {
		return "i1 " + ((this.value)?"1":"0");
	}
	
	public boolean getValue(){
		return this.value;
	}
	
	public String toTypeString(){
		return BasicType.TYPE_BOOL_STRING;
	}
}
