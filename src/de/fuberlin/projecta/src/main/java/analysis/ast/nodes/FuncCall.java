package analysis.ast.nodes;

import analysis.SymbolTableStack;
import parser.Tree;

/**
 * This class represents one function call. It has one or two children. The
 * first is id, which represents the functions name, the second is a node of
 * type Args (if existing) and contains all arguments.
 * 
 * @author micha
 * 
 */
public class FuncCall extends Tree {
	public void run(SymbolTableStack tables) {

	}
}
