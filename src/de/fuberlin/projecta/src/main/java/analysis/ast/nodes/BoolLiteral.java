package analysis.ast.nodes;

import analysis.SymbolTableStack;

public class BoolLiteral extends Statement {
	
	private boolean value;
	
	public BoolLiteral(boolean value) {
		this.value = value;
	}
	
	@Override
	public void buildSymbolTable(SymbolTableStack stack){
		
	}

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		return "i8 " + ((this.value)?"1":"0");
	}
	
	public boolean getValue(){
		return this.value;
	}
}
