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

package de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @author Johannes Dahlke
 *
 * @param <Value>
 */
@SuppressWarnings("rawtypes")
public abstract class InnerNode<Value> extends TreeNode<Value> implements Iterable<TreeNode> {
	
	private ArrayList<TreeNode> childNodes = new ArrayList<TreeNode>();
	
	@SuppressWarnings("unchecked")
	public InnerNode( Value value) {
		super( value);
	}
	
	public InnerNode( Value value, Value ... values) {
		super( value, values);
	}
	
	public InnerNode( InnerNode parentNode, Value value, Value ... values) {
		super( parentNode, value, values);
	}
	
	protected abstract boolean canAddChild( TreeNode childNode);
	
	/**
	 * Fügt ein Kindobjekt an das Ende der rechten Seite an.
	 */
	@SuppressWarnings("unchecked")
	public boolean addChild( TreeNode childNode) {
		if ( canAddChild( childNode)) {
			childNode.parentNode = this;
			childNodes.add( childNode);
			return true;
		} 
		return false;
	}
	
	/**
	 * Entfern ein Kindknoten.
	 * @param childNode Der Kindknoten, welcher entfernt werden soll.
	 * @return
	 */
	public boolean removeChild( TreeNode childNode) {
		return childNodes.remove( childNode);
	}
	
	/**
	 * Entfernt ein Kindknoten. 
	 * @param index Der Index des Jindknotens, welcher entfernt werden soll.
	 * @return
	 */
	public TreeNode removeChildWithIndex( int index) {
		return childNodes.remove( index);
	}
	
	/**
	 * Fügt einen neuen Kindknoten an gegebene Stelle ein.
	 * @param childNode Der einzufügende Kindknoten.
	 * @param index Die Stelle, an der Eingefügt werden soll.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean insertChild( TreeNode childNode, int index) {
		if ( canAddChild( childNode)) {
		  childNode.parentNode = this;
		  childNodes.add( index, childNode);
		  return true;
		}
		return false;
	}
	
	/**
	 * Liefert die Anzahl der Kindelemente.
	 * @return
	 */
	public int childCount() {
		return childNodes.size();
	}
	
	/**
	 * Liefert den Knoten mit dem gegebenen Index.
	 * @param index
	 * @return
	 */
	public TreeNode getNodeWithIndex( int index) {
		return childNodes.get( index);
	}
	
	/**
	 * Liefert den Index des gegebenen Kindknotens.
	 * @param treeNode
	 * @return
	 */
	public int getIndexOf( TreeNode treeNode) {
		return childNodes.indexOf( treeNode);
	}
	
	/**
	 * Liefert den gedrucken Baum mit allen Knoten.
	 * @return
	 */
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
		
		@SuppressWarnings("unchecked")
		InnerNode<Value> clonedInnerNode = (InnerNode<Value>) super.clone();
		clonedInnerNode.childNodes = new ArrayList<TreeNode>();
		for ( TreeNode<Value> thisChildNode : this.childNodes) {
			TreeNode clonedChildNode = (TreeNode) thisChildNode.clone();
			clonedChildNode.setParentNode( clonedInnerNode);
		}
		return clonedInnerNode;
	}

}
