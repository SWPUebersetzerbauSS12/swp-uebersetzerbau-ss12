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

package de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree;

import java.util.ArrayList;
import java.util.Iterator;

import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.InnerNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.Leaf;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNode;

import de.fuberlin.bii.utils.Test;

/**
 * Traversiert einen Baum aus TreeNode-Elementen von links nach rechts und von unten nach oben . 
 * 
 * @author Johannes Dahlke
 *
 */
@SuppressWarnings("rawtypes")
public class TreeIterator implements Iterator<TreeNode> {
	
	private TreeNode root;
	private TreeNode currentNode;
	private ArrayList<TreeNode> visitedNodes = new ArrayList<TreeNode>();
	
	
	public TreeIterator( Tree tree) {
		super();
		root = tree.getRoot();
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


	public TreeNode next() {
		TreeNode result = null;
		if ( currentNode instanceof InnerNode) {
			InnerNode currentInnerNode = (InnerNode) currentNode;
			for ( Object childNode : currentInnerNode) {
				if ( !visitedNodes.contains( childNode)) {
					currentNode = (TreeNode) childNode;
					return next();
				}
			}
			// all childs are processes. Then return yourself
			visitedNodes.add( currentInnerNode);
			currentNode = currentInnerNode.getParentNode();
			return currentInnerNode;
		} else {
			Leaf currentLeaf = (Leaf) currentNode;
			visitedNodes.add( currentLeaf);
			currentNode = currentLeaf.getParentNode();
			return currentLeaf;
		}
	}


	public void remove() {
		// TODO Auto-generated method stub

	}

}
