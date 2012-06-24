package de.fuberlin.projecta.analysis.ast.nodes;


public class Type extends AbstractSyntaxTree {

	@Override
	public boolean checkSemantics() {
		return true;
	}
	
	public String genCode(){
		return "";
	}

	@Override
	public boolean checkTypes() {
		return true;
	}
}
