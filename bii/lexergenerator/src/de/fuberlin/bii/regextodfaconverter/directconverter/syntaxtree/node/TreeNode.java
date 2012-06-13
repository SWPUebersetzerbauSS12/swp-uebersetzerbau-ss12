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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import regextodfaconverter.directconverter.syntaxtree.PrintHandler;
import utils.Test;


/**
 * 
 * @author Johannes Dahlke
 *
 * @param <Value>
 */
public class TreeNode<Value> implements Cloneable {

	protected List<Value> values;
	protected InnerNode parentNode = null;
	private PrintHandler printHandler = null;
	
	public TreeNode( Value value, Value ... values) {
		this( null, value, values);
	}
	
	public TreeNode( InnerNode parentNode, Value value, Value ... values) {
		super();
		this.parentNode = parentNode;
	  this.values = new ArrayList<Value>();
	  addValue( value);
	  addValues( values);
	}
	
	public Value getValue() {
		return values.get( 0);
	}
	
	public void addValue( Value value) {
		this.values.add( value);
	}

	public void addValues( Value ... values) {
		this.values.addAll( Arrays.asList( values));
	}
	
	public void addValues( List<Value> values) {
		this.values.addAll( values);
	}
	
	public void insertValue( int index, Value value) {
		this.values.add( index, value);
	}

	public void insertValues( int index, Value ... values) {
		this.values.addAll( index, Arrays.asList( values));
	}
	
	public void insertValues( int index, List<Value> values) {
		this.values.addAll( index, values);
	}
	
	public List<Value> getValues() {
		return values;
	}
	
	public InnerNode getParentNode() {
		return parentNode;
	}
	
	public boolean setParentNode( InnerNode newParentNode) {
		// case 1: change parent
		if ( Test.isAssigned(  this.parentNode) 
				&& Test.isAssigned( newParentNode) 
				&& newParentNode.canAddChild( this)) {
			this.parentNode.removeChild( this);
			newParentNode.addChild( this);
			return true;
		}
		// case 2: remove parent
		if ( Test.isAssigned(  this.parentNode)
				&& Test.isUnassigned( newParentNode)) {
			this.parentNode.removeChild( this);
			parentNode = null;
			return true;
		}
		// case 3: do nothing
		return false;
	}
	
	public void setParentNode( InnerNode newParentNode, int parentIndex) {
		if ( Test.isAssigned(  this.parentNode))
			this.parentNode.removeChild( this);
		if ( Test.isAssigned( newParentNode))
			newParentNode.insertChild( this, parentIndex);
		else 
			parentNode = null;
	}
	
	public boolean isRootNode() {
		return Test.isUnassigned( parentNode);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		TreeNode<Value> clonedTreeNode = (TreeNode<Value>) super.clone();
		clonedTreeNode.parentNode = this.parentNode;
		clonedTreeNode.values = new ArrayList<Value>( this.values);
		return clonedTreeNode;
	}
	
	@Override
	public String toString() {
		return Test.isAssigned( printHandler) 
				? printHandler.print( values.toArray())
			  : ( values.size() == 1
			        ? ( Test.isAssigned( values.get( 0)) 
			        		? values.get( 0).toString()
			        		: "null"
			        	)
			        : values.toString()
			    );
	}
	
	
	public void setPrintHandler( PrintHandler printHandler) {
		this.printHandler = printHandler;
	}
	
}
