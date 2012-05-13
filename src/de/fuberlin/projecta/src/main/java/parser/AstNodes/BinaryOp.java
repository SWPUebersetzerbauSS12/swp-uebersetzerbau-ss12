package parser.AstNodes;

import lombok.Getter;
import lombok.Setter;
import parser.ISyntaxTree;
import parser.Symbol;
import parser.Tree;
import semantic.analysis.SymbolTableStack;


public class BinaryOp extends Tree {
	@Getter
	@Setter
	ISyntaxTree left, right;

	public BinaryOp(Symbol symbol) {
		super(symbol);
	}

	public void run(SymbolTableStack tables) {

	}
}
