package analysis.ast.nodes;

import analysis.SymbolTableStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@AllArgsConstructor
public class BoolLiteral extends Statement {
	
	@Getter
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
}
