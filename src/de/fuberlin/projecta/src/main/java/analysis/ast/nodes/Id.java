package analysis.ast.nodes;

import lombok.AllArgsConstructor;
import analysis.SymbolTableStack;

@AllArgsConstructor
public class Id extends AbstractSyntaxTree {
	
	private String value;
	
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		//can't have children!
		return true;
	}

	@Override
	public String genCode() {
		return value;
	}
	
	public String getValue(){
		return this.value;
	}
}
