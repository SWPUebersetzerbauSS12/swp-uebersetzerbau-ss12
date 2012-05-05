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
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import utils.Notification;
import utils.Test;


public class SyntaxTree implements Iterable<NodeValue> {

	private static enum ParseMode {
		REGULAR_EXPRESSION, CHARACTER_CLASS, RANGE_SPECIFICATION
	}

	private static char[] REGEX_OPERATORS = { '[', '(', ')', '{', '|', '?', '+',
			'-', '*', '^', '$', '\\', '.'};

	private static char[] CHARACTER_CLASS_OPERATORS = { '^', ']', '-'};

	private static char[] RANGE_SPECIFICATION_OPERATORS = { ',', '}'};

	private static final char REGEX_ALTERNATIVE_CHAR = '|';
	private static final char REGEX_MASK_CHAR = '\\';
	private static final char REGEX_CLASS_BEGIN = '[';
	private static final char REGEX_CLASS_END = ']';
	private static final char REGEX_REPETITION_BEGIN = '{';
	private static final char REGEX_REPETITION_END = '}';
	private static final char REGEX_GROUP_BEGIN = '(';
	private static final char REGEX_GROUP_END = ')';
	private static final char REGEX_KLEENE_CLOSURE = '*';
	private static final char REGEX_POSITIVE_KLEENE_CLOSURE = '+';
	private static final char REGEX_OPTION = '?';
	private static final char REGEX_JOKER = '.';
	private static final char REGEX_EMPTY_STRING = 0x00;

	private BinaryTreeNode root = null;

	private ArrayBlockingQueue<Character> regexCharacters = null;

	private int blockCounter = 0;


	public SyntaxTree( String regex)
			throws SyntaxTreeException {
		super();
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


	private Character readNextChar( String errorMessage)
			throws SyntaxTreeException {
		Character result;
		if ( ( result = regexCharacters.poll()) == null)
			throw new SyntaxTreeException(
					"Expect a terminal. But there a no more characters to read.");
		return result;
	}


	private Terminal readTerminal() throws SyntaxTreeException {
		char readedChar = readNextChar( "Expect a terminal. But there a no more characters to read.");

		switch ( readedChar) {
			case REGEX_MASK_CHAR:
				readedChar = readNextChar( "Expect a terminal. But there a no more characters to read.");
				break;
			case REGEX_GROUP_BEGIN:
			case REGEX_GROUP_END:
				break;
			case REGEX_ALTERNATIVE_CHAR:
			case REGEX_CLASS_BEGIN:
			case REGEX_CLASS_END:
			case REGEX_REPETITION_BEGIN:
			case REGEX_REPETITION_END:
			case REGEX_KLEENE_CLOSURE:
			case REGEX_POSITIVE_KLEENE_CLOSURE:
			case REGEX_OPTION:
			case REGEX_JOKER:
				// we read the empty word
				readedChar = REGEX_EMPTY_STRING;
				break;
		// throw new SyntaxTreeException(
		// "Expect a terminal. But read a special char instead.");
		}
		return new Terminal( readedChar);
	}


	private boolean isSpecialChar( char theChar) {
		switch ( theChar) {
			case REGEX_MASK_CHAR:
			case REGEX_GROUP_BEGIN:
			case REGEX_GROUP_END:
			case REGEX_ALTERNATIVE_CHAR:
			case REGEX_CLASS_BEGIN:
			case REGEX_CLASS_END:
			case REGEX_REPETITION_BEGIN:
			case REGEX_REPETITION_END:
			case REGEX_KLEENE_CLOSURE:
			case REGEX_POSITIVE_KLEENE_CLOSURE:
			case REGEX_OPTION:
			case REGEX_JOKER:
				return true;
			default:
				return false;
		}
	}


	private boolean isBasicOperator( char theChar) {
		switch ( theChar) {
			case REGEX_ALTERNATIVE_CHAR:
			case REGEX_KLEENE_CLOSURE:
				return true;
			default:
				return false;
		}
	}


	private Operator readOperation() throws SyntaxTreeException {
		Character nextChar;
		if ( ( nextChar = regexCharacters.peek()) != null) {
			switch ( nextChar) {
				case REGEX_ALTERNATIVE_CHAR:
					regexCharacters.poll();
					return new Operator( OperatorType.ALTERNATIVE);
				case REGEX_KLEENE_CLOSURE:
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

		if ( readedTerminal.getValue() == REGEX_GROUP_END) {
			blockCounter++;
			return null;
		}

		if ( readedTerminal.getValue() == REGEX_GROUP_BEGIN)
			blockCounter--;

		BinaryTreeNode leafNode = ( readedTerminal.getValue() == REGEX_GROUP_BEGIN) ? buildSubTree()
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

		return new BinaryTreeNode( operator, leftNode, rightNode);
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
		for ( NodeValue nodeValue : this) {
			if ( !( ( nodeValue instanceof Terminal) && ( ( (Terminal) nodeValue)
					.getValue() == REGEX_EMPTY_STRING)))
				result += nodeValue + "  ";
			else
				result += "ε  ";
		}
		return result;
	}


	public Iterator<NodeValue> iterator() {
		return new SyntaxTreeIterator( this);
	}


	public BinaryTreeNode getRoot() {
		return root;
	}


	/**
	 * Prüft, ob der Teilbaum das Lesen des leere Wort ermöglicht.
	 * 
	 * @param node
	 * @return
	 */
	private boolean nullable( BinaryTreeNode node) {
		// \epsilon-Knoten sind per definition true
		if ( node.nodeValue instanceof Terminal) {
		  if (((Terminal) node.nodeValue).getValue() == REGEX_EMPTY_STRING)
			  return true;
		  else // Terminale != \epsilon sind nicht nullable
		  	return false;
		} else { // der Knoten enthält eine Operation 
			Operator operator = (Operator) node.nodeValue;
		  switch ( operator.getOperatorType()) {
		  	case ALTERNATIVE :
		  	  return nullable( node.leftChildNode) ||
		  	  	   	 nullable( node.rightChildNode);
		  	case CONCATENATION :
		  		return nullable( node.leftChildNode) &&
	  	   	       nullable( node.rightChildNode);
		  	default: //REPETITION
			  	return true;
		  }
		}
	}
	
	
	/**
	 * Liefert eine Sammlung aller Knoten, die bei Worten gebildet über den Unterbaum ab diesem Knoten an erste Stelle stehen können. 
	 * @param node
	 * @return
	 */
	private Collection<BinaryTreeNode> firstpos( BinaryTreeNode node) {
		  // \epsilon-Knoten liefern per definition die leere Menge
			if ( node.nodeValue instanceof Terminal) {
			  if (((Terminal) node.nodeValue).getValue() == REGEX_EMPTY_STRING)
				  return new ArrayList<BinaryTreeNode>();
			  else { // Terminale != \epsilonliefern das aktuelle Element
			  	Collection<BinaryTreeNode> result = new ArrayList<BinaryTreeNode>();
				  result.add( node);
			    return result;
			  }
			} else { // der Knoten enthält eine Operation 
				Operator operator = (Operator) node.nodeValue;
			  switch ( operator.getOperatorType()) {
			  	case ALTERNATIVE : {// Vereinigung der firstpos-Mengen
			  		Collection<BinaryTreeNode> result = firstpos( node.leftChildNode);
			  		result.addAll( firstpos( node.rightChildNode));
			  	  return result;
			  	}
			  	case CONCATENATION :
			  		if ( nullable( node.leftChildNode)) {
			  			Collection<BinaryTreeNode> result = firstpos( node.leftChildNode);
				  		result.addAll( firstpos( node.rightChildNode));
				  	  return result;
			  		} else {
			  			return firstpos( node.leftChildNode);
			  		}
			    default: //REPETITION
				  	return firstpos( node.leftChildNode);
			  }
			}	
	}
	
	
	/**
	 * Liefert eine Sammlung aller Knoten, die am Ende eines Wortes stehen können, welches über den Unterbaum den Knoten n gebildet werden können. 
	 * @param node
	 * @return
	 */
	private Collection<BinaryTreeNode> lastpos( BinaryTreeNode node) {
		  // \epsilon-Knoten liefern per definition die leere Menge
			if ( node.nodeValue instanceof Terminal) {
			  if (((Terminal) node.nodeValue).getValue() == REGEX_EMPTY_STRING)
				  return new ArrayList<BinaryTreeNode>();
			  else { // Terminale != \epsilon liefern das aktuelle Element
			  	Collection<BinaryTreeNode> result = new ArrayList<BinaryTreeNode>();
				  result.add( node);
			    return result;
			  }
			} else { // der Knoten enthält eine Operation 
				Operator operator = (Operator) node.nodeValue;
			  switch ( operator.getOperatorType()) {
			  	case ALTERNATIVE : {// Vereinigung der firstpos-Mengen
			  		Collection<BinaryTreeNode> result = lastpos( node.leftChildNode);
			  		result.addAll( firstpos( node.rightChildNode));
			  	  return result;
			  	}
			  	case CONCATENATION :
			  		if ( nullable( node.leftChildNode)) {
			  			Collection<BinaryTreeNode> result = lastpos( node.leftChildNode);
				  		result.addAll( lastpos( node.rightChildNode));
				  	  return result;
			  		} else {
			  			return lastpos( node.rightChildNode);
			  		}
			    default: //REPETITION
				  	return firstpos( node.leftChildNode);
			  }
			}	
	}
		
	

}
