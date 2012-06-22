package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.parser.ISyntaxTree;



public abstract class Statement extends AbstractSyntaxTree {
	
	@Override
	public boolean checkSemantics() {
		return true;
	}
	
	public Block getHighestBlock(){
		Block block = null;
		if(getParent() != null){
			ISyntaxTree parent = getParent();
			while(parent != null){
				if(parent instanceof Block){
					block = (Block) parent;					
				}
				parent = parent.getParent();
			}
		}		
		return block;
	}
}
