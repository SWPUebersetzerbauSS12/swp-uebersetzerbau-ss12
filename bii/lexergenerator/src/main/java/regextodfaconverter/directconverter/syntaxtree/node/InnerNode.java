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

package regextodfaconverter.directconverter.syntaxtree.node;

import java.util.ArrayList;
import java.util.Iterator;

import regextodfaconverter.directconverter.syntaxtree.PrintHandler;
import utils.Test;


/**
 * Liefert den Wert für einen inneren Knoten in dem {@link SyntaxTree Syntaxbaum}.
 * 
 * @author Johannes Dahlke
 *
 */
public class InnerNode<Value> extends TreeNode<Value> implements Iterable<TreeNode> {	
  
	private ArrayList<TreeNode> childNodes = new ArrayList<TreeNode>();
	
	public InnerNode( Value value) {
		super( value);
	}

	/**
	 * Fügt ein Kindobjekt an das Ende der rechten Seite an.
	 */
	public void addChild( TreeNode childNode) {
		childNode.parentNode = this;
    childNodes.add( childNode);
	}
	
	public void insertChild( TreeNode childNode, int index) {
		childNode.parentNode = this;
		childNodes.add( index, childNode);
	}
	
	
	public boolean removeChild( TreeNode childNode) {
		return childNodes.remove( childNode);
	}
	
	public TreeNode getNodeWithIndex( int index) {
		return childNodes.get( index);
	}
	
	public int childCount() {
		return childNodes.size();
	}
	
	
	
	public String toFullString() {
		String result = toString() + ": ";
		String childs = "";
		for ( TreeNode child : childNodes) {
			childs += childs.isEmpty() ? "( " : ", ";
			if ( child instanceof InnerNode)
				childs += ((InnerNode)child).toFullString();
			else 
				childs += child.toString();
		}
		childs += childs.isEmpty() ? "" : " )";
		result += childs;
		return result;
	}

	public Iterator<TreeNode> iterator() {
		return childNodes.iterator();
	}
	
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		InnerNode<Value> clonedInnerNode = (InnerNode<Value>) super.clone();
		clonedInnerNode.childNodes = new ArrayList<TreeNode>();
		for ( TreeNode<Value> thisChildNode : this.childNodes) {
			TreeNode clonedChildNode = (TreeNode) thisChildNode.clone();
			clonedChildNode.setParentNode( clonedInnerNode);
		}
		return clonedInnerNode;
	}

	public int getIndexOf( TreeNode treeNode) {
		return childNodes.indexOf( treeNode);
	}
	
}

