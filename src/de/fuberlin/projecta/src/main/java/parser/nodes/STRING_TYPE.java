package parser.nodes;

import semantic.analysis.SymbolTableStack;


public class STRING_TYPE extends Terminal {

	public STRING_TYPE(String name) {
		super(name);
	}

	@Override
	public void run(SymbolTableStack tables) {
		addAttribute("type");
		setAttribute("type", "String");
	}
}
