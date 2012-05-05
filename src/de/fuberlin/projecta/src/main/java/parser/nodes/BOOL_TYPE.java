package parser.nodes;

import semantic.analysis.SymbolTableStack;


public class BOOL_TYPE extends Terminal {

	public BOOL_TYPE(String name) {
		super(name);
	}

	@Override
	public void run(SymbolTableStack tables) {
		addAttribute("type");
		setAttribute("type", "boolean");
	}
}
