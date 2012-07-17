package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.projecta.analysis.SymbolTableHelper;

/**
 * Must have exactly two children of the type Id! First id is the record id,
 * second is the variable, which is accessed right now.
 */
public class RecordVarCall extends Expression {

	public Id getRecordId() {
		return (Id) getChild(0);
	}

	public Id getVarId() {
		return (Id) getChild(1);
	}

	@Override
	public String toTypeString() {
		return SymbolTableHelper
				.lookup(getRecordId().getValue(), getVarId().getValue(), this)
				.getType().toTypeString();
	}
}
