package parser.AstNodes;

import lombok.Getter;
import lombok.Setter;
import parser.ISyntaxTree;
import parser.Symbol;
import parser.Tree;
import semantic.analysis.SymbolTableStack;


public class CMP_OP extends Tree {

	@Getter
	@Setter
	ISyntaxTree left, right;

	public CMP_OP(Symbol symbol) {
		super(symbol);
	}

	public void run(SymbolTableStack tables) {

	}
}
