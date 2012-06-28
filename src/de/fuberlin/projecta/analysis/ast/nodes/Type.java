package de.fuberlin.projecta.analysis.ast.nodes;


public abstract class Type extends AbstractSyntaxTree {
	
	public static final String TYPE_BOOL_STRING = "bool";
	public static final String TYPE_REAL_STRING = "real";
	public static final String TYPE_INT_STRING = "int";
	public static final String TYPE_STRING_STRING = "string";
	public static final String TYPE_VOID_STRING = "void";

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
