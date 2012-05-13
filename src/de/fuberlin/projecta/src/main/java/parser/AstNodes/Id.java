package parser.AstNodes;

import lombok.Getter;
import lombok.Setter;
import parser.Symbol;
import parser.Tree;
import semantic.analysis.SymbolTableStack;


public class Id extends Tree {
	@Getter
	@Setter
	String name;

	public Id(Symbol symbol) {
		super(symbol);
	}

	public void run(SymbolTableStack tables) {

	}
}
