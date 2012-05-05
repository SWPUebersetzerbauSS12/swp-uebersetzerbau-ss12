package regextodfaconverter.directconverter;

import java.util.ArrayList;
import java.util.Iterator;

import utils.Test;


class SyntaxTreeIterator implements Iterator<NodeValue> {
	
	private BinaryTreeNode root;
	private BinaryTreeNode currentNode;
	private ArrayList<BinaryTreeNode> visitedNodes = new ArrayList<BinaryTreeNode>();
	
	
	public SyntaxTreeIterator( SyntaxTree syntaxTree) {
		super();
		root = syntaxTree.getRoot();
		currentNode = root; 
	}

	public boolean hasNext() {
		if ( Test.isUnassigned( root))
		  return false;
		if ( visitedNodes.contains( root))
			return false;
		// otherwise there are further elements
		return true;
	}


	public NodeValue next() {
		NodeValue result = null;
			while ( Test.isAssigned( currentNode.leftChildNode)
					&& !visitedNodes.contains( currentNode.leftChildNode))
				currentNode = currentNode.leftChildNode;
			if ( Test.isUnassigned( currentNode.leftChildNode)) {
				visitedNodes.add( currentNode);
				result = currentNode.nodeValue;
				currentNode = currentNode.parentNode;
			} else if ( Test.isAssigned( currentNode.rightChildNode) 
					        && !visitedNodes.contains( currentNode.rightChildNode)) {
				currentNode = currentNode.rightChildNode;
				result = next();
			} else {
				visitedNodes.add( currentNode);
				result = currentNode.nodeValue;
				currentNode = currentNode.parentNode;
			}

		return result;
	}


	public void remove() {
		// TODO Auto-generated method stub

	}

}
