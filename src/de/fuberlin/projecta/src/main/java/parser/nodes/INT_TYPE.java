package parser.nodes;

import semantic.analysis.SymbolTableStack;


public class INT_TYPE extends Terminal {

	public INT_TYPE(String name) {
		super(name);
	}

	@Override
	public void run(SymbolTableStack tables) {
		addAttribute("type");
		setAttribute("type", "int");
	}
}
