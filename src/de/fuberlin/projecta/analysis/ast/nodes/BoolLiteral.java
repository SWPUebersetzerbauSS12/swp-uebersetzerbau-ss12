package de.fuberlin.projecta.analysis.ast.nodes;


public class BoolLiteral extends Literal {
	
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
	
	public String toTypeString(){
		return TYPE_BOOL_STRING;
	}
}
