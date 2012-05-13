package parser.AstNodes;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import parser.Symbol;
import parser.Tree;
import semantic.analysis.SymbolTableStack;


public class Program extends Tree {
	@Getter
	private List<FuncDef> functions;

	public void addFunction(FuncDef func){
		functions.add(func);
	}

	public Program(Symbol symbol) {
		super(symbol);
		functions = new ArrayList<FuncDef>();
	}

	public void run(SymbolTableStack tables) {

	}
}
