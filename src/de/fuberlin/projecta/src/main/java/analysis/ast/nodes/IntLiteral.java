package analysis.ast.nodes;

import analysis.SymbolTableStack;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IntLiteral extends Statement {
	
	private int value;
	
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		return "i32 " + this.value;
	}
	
	public int getValue(){
		return this.value;
	}
}
