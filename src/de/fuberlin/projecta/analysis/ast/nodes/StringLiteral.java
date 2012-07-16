package de.fuberlin.projecta.analysis.ast.nodes;


public class StringLiteral extends Expression {

	private String value;

	public StringLiteral(String value) {
		this.value = value;
	}
	
	@Override
	public String genCode() {
		return "c\"" + this.value  + "\\00" + "\"";
	}

	public int getLength(){
		return value.length();
	}
	
	public String getValue() {
		return this.value;
	}
	
	@Override
	public String toTypeString(){
		return Type.TYPE_STRING_STRING;
	}
}
