package analysis.ast.nodes;

import lombok.AllArgsConstructor;
import analysis.SymbolTableStack;

@AllArgsConstructor
public class BoolLiteral extends Statement {
	
	private boolean value;
	
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
