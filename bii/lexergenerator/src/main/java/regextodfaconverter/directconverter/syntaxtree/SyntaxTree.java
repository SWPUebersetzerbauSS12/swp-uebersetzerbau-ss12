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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import regextodfaconverter.directconverter.RegexSpecialChars;
import regextodfaconverter.directconverter.lr0parser.ItemAutomata;
import regextodfaconverter.directconverter.lr0parser.Lr0ItemAutomata;
import regextodfaconverter.directconverter.lr0parser.ReduceEventHandler;
import regextodfaconverter.directconverter.lr0parser.ShiftEventHandler;
import regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import regextodfaconverter.directconverter.lr0parser.grammar.Grammar;
import regextodfaconverter.directconverter.lr0parser.grammar.Grammars;
import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionSet;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.syntaxtree.node.BinaryTreeNode;
import regextodfaconverter.directconverter.syntaxtree.node.InnerNode;
import regextodfaconverter.directconverter.syntaxtree.node.Leaf;
import regextodfaconverter.directconverter.syntaxtree.node.NewNodeEventHandler;
import regextodfaconverter.directconverter.syntaxtree.node.NodeValue;
import regextodfaconverter.directconverter.syntaxtree.node.Operator;
import regextodfaconverter.directconverter.syntaxtree.node.OperatorType;
import regextodfaconverter.directconverter.syntaxtree.node.TreeNode;

import utils.Notification;
import utils.Test;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public class SyntaxTree implements Iterable<TreeNode>, Cloneable {

	private ArrayList<Character> inputCharacters = null;

	private Stack<TreeNode> nodeStack = null;
	
	public NewNodeEventHandler onNewNodeEvent = null;

	private TreeNode rootNode = null;
	
	private SyntaxTreeAttributor annotations = null;

	private Grammar grammar;


	public SyntaxTree( ContextFreeGrammar grammar, String expression)
			throws SyntaxTreeException {
		this( grammar, expression, null);
	}
	
	/**
	 * Erweitert die Grammatik für reguläre  Ausdrücke um das Terminatorsymbol.
	 * @return
	 */
	private Grammar extendGrammar( Grammar grammar) {
		Grammar extendedGrammar = grammar;
		Nonterminal embracingNonterminal = new Nonterminal();
		Terminal<Character> terminator = new Terminal<Character>( RegexSpecialChars.TERMINATOR);
		ProductionSet productions = new ProductionSet();
		extendedGrammar.addProduction( new ProductionRule( embracingNonterminal, extendedGrammar.getStartSymbol(), terminator));
		extendedGrammar.setStartSymbol( embracingNonterminal);
		return extendedGrammar;
	}
	
	public SyntaxTree(  ContextFreeGrammar grammar, String expression, NewNodeEventHandler newNodeEventHandler)
			throws SyntaxTreeException {
		super();
		this.grammar = extendGrammar( grammar);
		this.onNewNodeEvent = newNodeEventHandler;
		String terminizedExpression = "(" + expression + ")" + RegexSpecialChars.TERMINATOR;
		initTreeSkeleton();
		preprocessInput( terminizedExpression);
		buildTree();
	}


	private void buildTree() {
		ItemAutomata<Character> itemAutomata = new Lr0ItemAutomata<Character>( (ContextFreeGrammar) grammar);
		
		itemAutomata.setReduceEventHandler( getReduceEventHandler());
		
		itemAutomata.setShiftEventHandler( getShiftEventHandler());

		itemAutomata.match( inputCharacters);
		
		rootNode = nodeStack.peek();
	}
	

	protected ReduceEventHandler getReduceEventHandler() {
		return new ReduceEventHandler() {
			
			public Object handle( Object sender, Nonterminal nonterminal, int countOfReducedElements, int countOfLeftElementsOnStack) throws Exception {
			  System.out.println( "reduce to " + nonterminal + ". Reduced elements: " + countOfReducedElements + " Left elements: " + countOfLeftElementsOnStack);
				
			  // create new inner node
			  String nonterminalName = nonterminal.toString();
				InnerNode newInnerNode = new InnerNode( nonterminalName);
				
				if ( Test.isAssigned( onNewNodeEvent))
					onNewNodeEvent.doOnEvent( this, newInnerNode);
				
				// add childs to the new inner node
			  for ( int i = 0; i < countOfReducedElements; i++) {
			  	TreeNode childNode = nodeStack.pop();
			  	newInnerNode.insertChild( childNode, 0);
			  	if ( childNode instanceof InnerNode)
			  	  System.out.println( "pop: " +((InnerNode)childNode).toFullString());
			  	else
			  	  System.out.println( "pop: " +childNode);
			  		
				}
			  
				// push the inner node onto stack
				nodeStack.push( newInnerNode);
					
				return null;
			}
		};
	}


	protected ShiftEventHandler getShiftEventHandler() {
		return new ShiftEventHandler() {

			public Object handle( Object sender, Terminal shiftedTerminal) throws Exception {
				System.out.println( "shift " + shiftedTerminal);
				Comparable terminalSymbol = shiftedTerminal.getSymbol();
				
				Leaf newLeaf = new Leaf( terminalSymbol);
				
				if ( Test.isAssigned( onNewNodeEvent))
					onNewNodeEvent.doOnEvent( this, newLeaf);

				nodeStack.push( newLeaf);
				return null;
			}
		};
	}

	private void initTreeSkeleton() {
		nodeStack = new Stack<TreeNode>();
	}
	
	private void preprocessInput( String inputString) {
		inputCharacters = new ArrayList<Character>();
		for ( Character inputCharacter : inputString.toCharArray()) {
		  inputCharacters.add( inputCharacter);
		}
	}
	
	

	public SyntaxTreeAttributor getAnnotations() {
		return annotations;
	}
	
	
	public void setAnnotations( SyntaxTreeAttributor annotations) {
		this.annotations = annotations;
	}
	
	public Iterator<TreeNode> iterator() {
		return new SyntaxTreeIterator( this);
	}


	public TreeNode getRoot() {
		return nodeStack.peek();
	}

	
	public Collection<Character> getCharacterSet() {
		Collection<Character> characters = new HashSet<Character>();
		for ( TreeNode node : this) {
			if ( Test.isAssigned( node)
					&& node instanceof Leaf) {
				Character currentTerminal = (Character)( (Leaf) node).getValue();
			}
		}
		return characters; 
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		SyntaxTree clonedTree = (SyntaxTree) super.clone();
		
		clonedTree.grammar = this.grammar;
		clonedTree.rootNode = (TreeNode) this.rootNode.clone();
		
		return clonedTree;
	}
		
	
	public static SyntaxTree compress( SyntaxTree originalTree) {
		
		// we work on a copy
		SyntaxTree clonedTree;
		try {
			clonedTree = (SyntaxTree) originalTree.clone();
		} catch ( CloneNotSupportedException e) {
			Notification.printDebugException( e);
			return null;
		}
		
		// determine all nodes that can be skipped
		ArrayList<InnerNode> nodesToSkip = new ArrayList<InnerNode>();
		for ( TreeNode treeNode : clonedTree) {
			if ( treeNode instanceof InnerNode) {
				InnerNode innerTreeNode = (InnerNode) treeNode;
				if ( innerTreeNode.childCount() == 1 
						&& Test.isAssigned( innerTreeNode.getParentNode())
						&& innerTreeNode.getParentNode() instanceof InnerNode) {
					nodesToSkip.add( innerTreeNode);
				} 
			}
		}
		
		// remove singles 
		for ( InnerNode innerTreeNode : nodesToSkip) {
			InnerNode innerTreeNodeParent = (InnerNode) innerTreeNode.getParentNode();
			TreeNode childNode = innerTreeNode.getNodeWithIndex( 0);
			childNode.setParentNode( innerTreeNodeParent);
			innerTreeNode.setParentNode( null);
		}
		return clonedTree;
	}

	
	@Override
	public String toString() {
		return Test.isAssigned( rootNode) 
				? (( rootNode instanceof InnerNode) 
						? ((InnerNode)rootNode).toFullString() 
						: rootNode.toString()) 
				: super.toString();
	}
}
