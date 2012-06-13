package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;


public abstract class Statement extends AbstractSyntaxTree {

	public void buildSymbolTable(SymbolTableStack tables) {

	}
	
	@Override
	public boolean checkSemantics() {
		return true;
	}
}
