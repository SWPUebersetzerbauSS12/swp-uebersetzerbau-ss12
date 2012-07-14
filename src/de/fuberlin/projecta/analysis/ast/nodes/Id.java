package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableHelper;

public class Id extends Type {

	/**
	 * Should be set in genCode, when register is allocated
	 */

	private String value;

	public Id(String value) {
		this.value = value;
	}

	@Override
	public String genCode() {
		return value;
	}

	public String getValue() {
		return this.value;
	}

	public Type getType() {
		if (SymbolTableHelper.lookup(this.getValue(), this) != null)
			return SymbolTableHelper.lookup(this.getValue(), this).getType();
		return null;
	}

	@Override
	public String toTypeString() {
		if (getType() != null)
			return getType().toTypeString();
		return "";
	}
}
