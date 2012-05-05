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

package regextodfaconverter.directconverter;

import utils.Test;


/**
 * Stellt einen Konten des {@link SyntaxTree Syntaxbaumes} dar.
 * 
 * @author Johannes Dahlke
 *
 */
class BinaryTreeNode {
	
	NodeValue nodeValue; 
	BinaryTreeNode leftChildNode;
	BinaryTreeNode rightChildNode;
	BinaryTreeNode parentNode;
	
	public BinaryTreeNode( NodeValue nodeValue, BinaryTreeNode leftChildNode, BinaryTreeNode rightChildNode) {
		super();
		this.nodeValue = nodeValue;
		
		this.leftChildNode = leftChildNode;
		if ( Test.isAssigned( this.leftChildNode))
  		this.leftChildNode.parentNode = this;
		
		this.rightChildNode = rightChildNode;
		if ( Test.isAssigned( this.rightChildNode))
		  this.rightChildNode.parentNode = this;
	}
	
}