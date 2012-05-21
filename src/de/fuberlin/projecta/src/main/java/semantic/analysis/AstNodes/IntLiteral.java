package semantic.analysis.AstNodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import semantic.analysis.SymbolTableStack;

@AllArgsConstructor
public class IntLiteral extends Statement {
	
	@Getter
	private int value;
	
	public void run(SymbolTableStack tables) {

	}
}
