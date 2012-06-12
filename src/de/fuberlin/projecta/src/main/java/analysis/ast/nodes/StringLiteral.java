package analysis.ast.nodes;

import analysis.SymbolTableStack;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StringLiteral extends Statement {
	
	private String value;
	
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		return this.value + "\\00";
	}
	
	public String getValue() {
		return this.value;
	}
}
