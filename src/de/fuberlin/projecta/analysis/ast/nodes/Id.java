package de.fuberlin.projecta.analysis.ast.nodes;


public class Id extends AbstractSyntaxTree {
	
	/**
	 * Should be set in genCode, when register is allocated
	 */
	
	private String value;
	
	public Id(String value){
		this.value = value;
	}
	
	@Override
	public boolean checkSemantics() {
		//can't have children!
		return true;
	}

	@Override
	public String genCode() {
		return value;
	}
	
	public String getValue(){
		return this.value;
	}

	@Override
	public boolean checkTypes() {
		// an identifier by itself is ok.
		return true;
	}
}
