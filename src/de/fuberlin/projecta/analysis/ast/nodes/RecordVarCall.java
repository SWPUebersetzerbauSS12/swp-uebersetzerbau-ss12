package de.fuberlin.projecta.analysis.ast.nodes;


/**
 * Must have exactly two children of the type Id! First id is the record id,
 * second is the variable, which is accessed right now.
 * 
 * @author sh4ke
 */
public class RecordVarCall extends AbstractSyntaxTree {

	@Override
	public boolean checkSemantics() {
		// Any situation where this could be ambiguous???
		return true;
	}

	@Override
	public String genCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}
