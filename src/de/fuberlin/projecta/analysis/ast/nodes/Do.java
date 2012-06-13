package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;

/**
 * root node of do-while-loop
 *
 */
public class Do extends Statement {
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		for(int i = 0; i < this.getChildrenCount(); i++){
			if(!((AbstractSyntaxTree)this.getChild(i)).checkSemantics()){
				return false;
			}
		}
		return true;
	}

	@Override
	public String genCode() {
		// TODO Auto-generated method stub
		return null;
	}
}
