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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import utils.Notification;
import utils.Test;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public class SyntaxTree implements Iterable<BinaryTreeNode> {


	private BinaryTreeNode root = null;

	private ArrayBlockingQueue<Character> regexCharacters = null;
	
	public NewNodeEventHandler onNewParentNode = null;

	private SyntaxTreeAttributor annotations = null;

	private int blockCounter = 0;


	public SyntaxTree( String regex)
			throws SyntaxTreeException {
		this( regex, null);
	}
	
	public SyntaxTree( String regex, NewNodeEventHandler newNodeEventHandler)
			throws SyntaxTreeException {
		super();
		this.onNewParentNode = newNodeEventHandler;
		init( regex);
		buildTree();
		if ( blockCounter != 0)
			throw new SyntaxTreeException(
					"Invalid regular expression. There are brackets missing.");
	}


	private void init( String regex) {
		blockCounter = 0;
		regexCharacters = new ArrayBlockingQueue<Character>( regex.length());
		for ( Character regexChar : regex.toCharArray()) {
			try {
				regexCharacters.put( regexChar);
			} catch ( InterruptedException e) {
				Notification.printDebugException( e);
			}
		}
	}
	
	
	public SyntaxTreeAttributor getAnnotations() {
		return annotations;
	}
	
	
	public void setAnnotations( SyntaxTreeAttributor annotations) {
		this.annotations = annotations;
	}
	

	private Character readNextChar( String errorMessage)
			throws SyntaxTreeException {
		Character result;
		if ( ( result = regexCharacters.poll()) == null)
			throw new SyntaxTreeException( errorMessage);
		return result;
	}

	private Character testNextChar() {
		return regexCharacters.peek();
	}

	private Terminal readTerminal() throws SyntaxTreeException {
		char readedChar;
		
		if ( RegexSpecialChars.isBasicOperator( testNextChar())) {
		  // we read the empty word
			readedChar = RegexSpecialChars.EMPTY_STRING;
		} else {
			readedChar = readNextChar( "Expect a terminal. But there a no more characters to read.");
			if ( RegexSpecialChars.REGEX_MASK_CHAR == readedChar)
				readedChar = readNextChar( "Expect a terminal. But there a no more characters to read.");
		  
			// otherwise process the readed char as is
			// it could be a terminal or grouping bracket solely 			
		}
		
		return new Terminal( readedChar);
	}



	private Operator readOperation() throws SyntaxTreeException {
		Character nextChar;
		if ( ( nextChar = regexCharacters.peek()) != null) {
			switch ( nextChar) {
				case RegexSpecialChars.REGEX_ALTERNATIVE_CHAR:
					regexCharacters.poll();
					return new Operator( OperatorType.ALTERNATIVE);
				case RegexSpecialChars.REGEX_KLEENE_CLOSURE:
					regexCharacters.poll();
					return new Operator( OperatorType.REPETITION);
				default:
					return new Operator( OperatorType.CONCATENATION);
			}
		}
		throw new SyntaxTreeException(
				"Expect a operator. But there a no more characters to read.");
	}


	private BinaryTreeNode createChildNode() throws SyntaxTreeException {

		Terminal readedTerminal = readTerminal();

		if ( readedTerminal.getValue() == RegexSpecialChars.REGEX_GROUP_END) {
			blockCounter++;
			return null;
		}

		if ( readedTerminal.getValue() == RegexSpecialChars.REGEX_GROUP_BEGIN)
			blockCounter--;

		BinaryTreeNode leafNode = ( readedTerminal.getValue() == RegexSpecialChars.REGEX_GROUP_BEGIN) ? buildSubTree()
				: new BinaryTreeNode( readedTerminal, null, null);

		return leafNode;
	}


	private BinaryTreeNode createParentNode( BinaryTreeNode leftNode)
			throws SyntaxTreeException {

		if ( Test.isUnassigned( leftNode)) {
			leftNode = createChildNode();
		}
		if ( Test.isUnassigned( leftNode))
			return null;

		Operator operator;
		try {
			operator = readOperation();
		} catch ( Exception e) {
			// Notification.printDebugException( e);
			return null;
		}

		BinaryTreeNode rightNode = null;
		if ( operator.getOperatorType().isBinary()) {
			rightNode = createChildNode();
			if ( Test.isUnassigned( rightNode)) {
				return null;
			}
		}

		BinaryTreeNode newNode = new BinaryTreeNode( operator, leftNode, rightNode);
		if ( Test.isAssigned( onNewParentNode)) {
			onNewParentNode.doOnEvent( this, newNode);
		}
		
		return newNode;
	}


	private BinaryTreeNode buildSubTree() throws SyntaxTreeException {
		BinaryTreeNode currentNode = null;
		BinaryTreeNode previousNode = currentNode;
		while ( ( currentNode = createParentNode( currentNode)) != null) {
			previousNode = currentNode;
			if ( Test.isAssigned( currentNode))
				root = currentNode;
		}
		return previousNode;
	}


	private void buildTree() throws SyntaxTreeException {
		BinaryTreeNode currentNode = null;
		while ( ( currentNode = createParentNode( currentNode)) != null) {
			if ( Test.isAssigned( currentNode))
				root = currentNode;
		}
	}


	@Override
	public String toString() {
		String result = "";
		for ( BinaryTreeNode node : this) {
			if ( !( ( node.nodeValue instanceof Terminal) && ( ( (Terminal) node.nodeValue)
					.getValue() == RegexSpecialChars.EMPTY_STRING)))
				result += node.nodeValue + "  ";
			else
				result += "ε  ";
		}
		return result;
	}


	public Iterator<BinaryTreeNode> iterator() {
		return new SyntaxTreeIterator( this);
	}


	public BinaryTreeNode getRoot() {
		return root;
	}



}
