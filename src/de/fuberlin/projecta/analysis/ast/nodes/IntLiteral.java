package de.fuberlin.projecta.analysis.ast.nodes;


public class IntLiteral extends Literal {
	
	private int value;
	
	public IntLiteral(int value){
		this.value = value;
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
		return Type.TYPE_INT_STRING;
	}
}
