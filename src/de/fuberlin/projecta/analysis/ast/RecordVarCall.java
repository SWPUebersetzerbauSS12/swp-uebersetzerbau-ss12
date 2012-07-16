package de.fuberlin.projecta.analysis.ast;

/**
 * Must have exactly two children of the type Id! First id is the record id,
 * second is the variable, which is accessed right now.
 */
public class RecordVarCall extends Expression {
	
	public Id getRecordId(){
		return (Id) getChild(0);
	}
	
	public Id getVarId(){
		return (Id) getChild(1);
	}
}
