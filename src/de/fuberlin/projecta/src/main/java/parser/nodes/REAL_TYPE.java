package parser.nodes;

import semantic.analysis.SymbolTableStack;


public class REAL_TYPE extends Terminal {

	public REAL_TYPE(String name) {
		super(name);
	}

	@Override
	public void run(SymbolTableStack tables) {
		addAttribute("type");
		setAttribute("type", "double");
	}
}
