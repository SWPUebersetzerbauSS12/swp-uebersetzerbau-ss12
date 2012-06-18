package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;

/**
 * This class represents one function call. It has one or two children. The
 * first is id, which represents the functions name, the second is a node of
 * type Args (if existing) and contains all arguments.
 * 
 * @author micha
 * 
 */
public class FuncCall extends AbstractSyntaxTree {
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

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}
