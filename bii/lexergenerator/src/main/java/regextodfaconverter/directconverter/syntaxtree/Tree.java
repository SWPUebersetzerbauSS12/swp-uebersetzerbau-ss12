package regextodfaconverter.directconverter.syntaxtree;

import regextodfaconverter.directconverter.lr0parser.grammar.Grammar;
import regextodfaconverter.directconverter.syntaxtree.node.TreeNode;


public interface Tree extends Iterable<TreeNode> {

	
	TreeNode getRoot();
	
	Grammar getGrammar();
	
}
