package parser.AstNodes;

import lombok.Getter;
import lombok.Setter;
import parser.Symbol;
import parser.Tree;
import semantic.analysis.SymbolTableStack;


public class Declaration extends Tree {
	@Getter
	@Setter
	private Type type;
	@Getter
	@Setter
	private Id id;

	public Declaration(Symbol symbol) {
		super(symbol);
	}

	public void run(SymbolTableStack tables) {

	}
}
