package analysis.ast.nodes;

import analysis.SymbolTableStack;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class IntLiteral extends Statement {
	
	@Getter
	private int value;
	
	public void run(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		// TODO Auto-generated method stub
		return false;
	}
}
