package analysis.ast.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import analysis.SymbolTableStack;

@AllArgsConstructor
public class Id extends AbstractSyntaxTree {
	
	@Getter
	private String value;
	
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		//can't have children!
		return true;
	}
}
