package analysis.ast.nodes;

import analysis.SymbolTableStack;


public class Break extends Statement {
	public void run(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		//We could erase dead code here, if we:
		// 1. have a look at the parent,
		// 2. Find out which child the break is
		// 3. Remove all children 'right' from break
		return true;
	}
}
