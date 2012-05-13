package parser.AstNodes;

import java.util.List;

import lombok.Getter;
import parser.Symbol;
import parser.Tree;
import semantic.analysis.SymbolTableStack;


public class Params extends Tree {
	@Getter
	private List<Declaration> params;

	public void addParameter(Declaration decl){
		params.add(decl);
	}

	public Params(Symbol symbol) {
		super(symbol);
	}

	public void run(SymbolTableStack tables) {

	}
}
