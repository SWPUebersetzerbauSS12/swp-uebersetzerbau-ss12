package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;

public class Args extends AbstractSyntaxTree {
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		// TODO Auto-generated method stub
		return null;
	}
}
