package de.fuberlin.projecta.analysis.ast.nodes;



public abstract class Statement extends AbstractSyntaxTree {
	
	@Override
	public boolean checkSemantics() {
		return true;
	}
	
	public Block getHighestBlock(){
		Block block = null;
		if(getParent() != null){
			AbstractSyntaxTree parent = (AbstractSyntaxTree) getParent();
			while(parent != null){
				if(parent instanceof Block){
					block = (Block) parent;					
				}
				parent = (AbstractSyntaxTree) parent.getParent();
			}
		}		
		return block;
	}
}
