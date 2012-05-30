package analysis.ast.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import analysis.SymbolTableStack;

@AllArgsConstructor
public class Id extends AbstractSyntaxTree {
	
	@Getter
	private String value;
	
	public void run(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		// TODO Auto-generated method stub
		return false;
	}
}
