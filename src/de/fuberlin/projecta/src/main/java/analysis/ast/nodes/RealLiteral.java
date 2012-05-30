package analysis.ast.nodes;

import analysis.SymbolTableStack;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RealLiteral extends Statement {
	
	@Getter
	private double value;
	
	public void run(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		// TODO Auto-generated method stub
		return false;
	}
}
