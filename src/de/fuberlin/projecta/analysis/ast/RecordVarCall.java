package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.analysis.TypeErrorException;

/**
 * Must have exactly two children of the type Id! First id is the record id,
 * second is the variable, which is accessed right now.
 */
public class RecordVarCall extends Expression {

	public Id getRecordId() {
		ISyntaxTree child = getChild(0);
		while(child instanceof RecordVarCall){
			child = child.getChild(0);
		}
		return (Id) child;
	}

	public Id getVarId() {
		return (Id) getChild(1);
	}

	@Override
	public String toTypeString() {
		Type a = SymbolTableHelper.lookupRecordVarCall(this);
		if(a == null){
			throw new TypeErrorException("Record not found");
		}
		return a.toTypeString();
	}
}
