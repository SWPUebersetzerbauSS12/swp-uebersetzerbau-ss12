package analysis.ast.nodes;

import analysis.SymbolTableStack;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RealLiteral extends Statement {
	
	private double value;
	
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		return "double " + this.value;
	}
	
	public double getValue(){
		return this.value;
	}
}
