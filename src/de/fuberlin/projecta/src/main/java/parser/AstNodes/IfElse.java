package parser.AstNodes;

import lombok.Getter;
import lombok.Setter;
import parser.Symbol;
import semantic.analysis.SymbolTableStack;


public class IfElse extends Statement {

	@Getter
	@Setter
	private Statement IfStatement;

	@Getter
	@Setter
	private Statement ElseStatement;

	public IfElse(Symbol symbol) {
		super(symbol);
	}

	public void run(SymbolTableStack tables) {

	}
}
