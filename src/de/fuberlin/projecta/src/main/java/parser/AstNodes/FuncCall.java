package parser.AstNodes;

import lombok.Getter;
import lombok.Setter;
import parser.Symbol;
import parser.Tree;
import semantic.analysis.SymbolTableStack;


public class FuncCall extends Tree {
	@Getter
	@Setter
	private Id id;
	@Getter
	@Setter
	private Params params;

	public FuncCall(Symbol symbol) {
		super(symbol);
	}

	public void run(SymbolTableStack tables) {

	}
}
