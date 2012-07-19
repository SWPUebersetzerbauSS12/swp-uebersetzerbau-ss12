package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableHelper;

/**
 * Must have exactly two children of the type Id! First id is the record id,
 * second is the variable, which is accessed right now.
 */
public class RecordVarCall extends Expression {

	/**
	 * Walks down the whole RecordVarCall trace until an Id is found.
	 * 
	 * @return The innermost record id in the possible record chain.
	 */
	public Id getRecordId() {
		ISyntaxTree child = getChild(0);
		while (child instanceof RecordVarCall) {
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
		if (a == null) {
			throw new SemanticException("Record not found", this);
		}
		return a.toTypeString();
	}
}
