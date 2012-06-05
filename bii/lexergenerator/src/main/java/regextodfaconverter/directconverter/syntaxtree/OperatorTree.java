package regextodfaconverter.directconverter.syntaxtree;

import regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import regextodfaconverter.directconverter.syntaxtree.node.NewNodeEventHandler;


public class OperatorTree extends SyntaxTree {

	public OperatorTree( ContextFreeGrammar grammar, String expression)
			throws SyntaxTreeException {
		super( grammar, expression);
	}

	public OperatorTree( ContextFreeGrammar grammar, String expression, NewNodeEventHandler newNodeEventHandler)
			throws SyntaxTreeException {
		super( grammar, expression, newNodeEventHandler);
	}
	

}
