/*
 * 
 * Copyright 2012 lexergen.
 * This file is part of lexergen.
 * 
 * lexergen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * lexergen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with lexergen.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * lexergen:
 * A tool to chunk source code into tokens for further processing in a compiler chain.
 * 
 * Projectgroup: bi, bii
 * 
 * Authors: Johannes Dahlke
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */


package regextodfaconverter.directconverter.syntaxtree;

import java.util.ArrayList;
import java.util.Iterator;

import regextodfaconverter.directconverter.syntaxtree.node.BinaryTreeNode;

import utils.Test;

/**
 * Iteriert von links nach rechts und von unten nach oben über einen Syntaxbaum. 
 * 
 * @author Johannes Dahlke
 *
 */
class SyntaxTreeIterator implements Iterator<BinaryTreeNode> {
	
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


	public BinaryTreeNode next() {
		BinaryTreeNode result = null;
			while ( Test.isAssigned( currentNode.leftChildNode)
					&& !visitedNodes.contains( currentNode.leftChildNode))
				currentNode = currentNode.leftChildNode;
			if ( Test.isUnassigned( currentNode.leftChildNode)) {
				visitedNodes.add( currentNode);
				result = currentNode;
				currentNode = currentNode.parentNode;
			} else if ( Test.isAssigned( currentNode.rightChildNode) 
					        && !visitedNodes.contains( currentNode.rightChildNode)) {
				currentNode = currentNode.rightChildNode;
				result = next();
			} else {
				visitedNodes.add( currentNode);
				result = currentNode;
				currentNode = currentNode.parentNode;
			}

		return result;
	}


	public void remove() {
		// TODO Auto-generated method stub

	}

}
