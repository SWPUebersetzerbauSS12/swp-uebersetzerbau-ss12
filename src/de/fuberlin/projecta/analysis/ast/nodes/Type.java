package de.fuberlin.projecta.analysis.ast.nodes;


public abstract class Type extends AbstractSyntaxTree {

	@Override
	public boolean checkSemantics() {
		return true;
	}
	
	public String genCode(){
		return "";
	}
	
	public String toTypeString(){
		return "";
	}
}
