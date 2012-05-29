package analysis.ast.nodes;

import analysis.SymbolTableStack;
import parser.Tree;

/**
 * Must have exactly two children of the type Id! First id is the record id,
 * second is the variable, which is accessed right now.
 * 
 * @author sh4ke
 */
public class RecordVarCall extends Tree {
	public void run(SymbolTableStack tables) {

	}
}
