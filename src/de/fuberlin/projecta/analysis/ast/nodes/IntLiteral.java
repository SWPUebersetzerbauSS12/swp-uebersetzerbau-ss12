package de.fuberlin.projecta.analysis.ast.nodes;


public class IntLiteral extends Type {
	
	private int value;
	
	public IntLiteral(int value){
		this.value = value;
	}

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		return "i32 " + this.value;
	}
	
	public int getValue(){
		return this.value;
	}
	
	@Override
	public String toTypeString(){
		return TYPE_INT_STRING;
	}
}