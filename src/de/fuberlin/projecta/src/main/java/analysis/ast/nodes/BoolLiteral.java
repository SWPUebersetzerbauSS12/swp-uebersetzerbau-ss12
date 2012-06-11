package analysis.ast.nodes;

import analysis.SymbolTableStack;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
		// TODO Auto-generated method stub
		return null;
	}
}
