package parser.AstNodes;

import lombok.Getter;
import lombok.Setter;
import parser.Symbol;
import semantic.analysis.SymbolTableStack;


public class Print extends Statement {

	@Getter
	@Setter
	private Id id;

	public Print(Symbol symbol) {
		super(symbol);
	}

	public void run(SymbolTableStack tables) {

	}
}
