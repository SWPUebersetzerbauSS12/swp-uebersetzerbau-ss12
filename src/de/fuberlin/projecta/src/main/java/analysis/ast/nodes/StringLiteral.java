package analysis.ast.nodes;

import analysis.SymbolTableStack;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StringLiteral extends Statement {
	
	@Getter
	private String value;
	
	public void run(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}
}
