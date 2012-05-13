package parser.AstNodes;

import lombok.Getter;
import lombok.Setter;
import parser.Symbol;
import semantic.analysis.SymbolTableStack;


public class Do extends Statement {

	@Getter
	@Setter
	private Statement statement;

	public Do(Symbol symbol) {
		super(symbol);
	}

	public void run(SymbolTableStack tables) {

	}
}
