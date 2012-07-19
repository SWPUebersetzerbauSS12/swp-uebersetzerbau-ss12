package de.fuberlin.projecta.analysis.ast;


public class StringLiteral extends Literal {

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
		return BasicType.TYPE_STRING_STRING;
	}
}
