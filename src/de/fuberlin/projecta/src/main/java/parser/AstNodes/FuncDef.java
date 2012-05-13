package parser.AstNodes;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import parser.Symbol;
import parser.Tree;
import semantic.analysis.SymbolTableStack;


public class FuncDef extends Tree {
	@Getter
	@Setter
	private Type type;
	@Getter
	@Setter
	private Id id;
	@Getter
	private List<Declaration> params;
	@Getter
	@Setter
	private Block block; // may be null since def int foobar(); is allowed

	public void addParam(Declaration decl){
		params.add(decl);
	}

	public FuncDef(Symbol symbol) {
		super(symbol);
		params = new ArrayList<Declaration>();
	}

	public void run(SymbolTableStack tables) {

	}
}
