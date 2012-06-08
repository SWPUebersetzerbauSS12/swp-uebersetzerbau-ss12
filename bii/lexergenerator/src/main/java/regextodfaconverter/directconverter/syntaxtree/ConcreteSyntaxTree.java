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

import regextodfaconverter.directconverter.EventHandler;
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
import regextodfaconverter.directconverter.regex.RegexSpecialChars;
import regextodfaconverter.directconverter.regex.operatortree.OperatorType;
import regextodfaconverter.directconverter.syntaxtree.node.InnerNode;
import regextodfaconverter.directconverter.syntaxtree.node.Leaf;
import regextodfaconverter.directconverter.syntaxtree.node.NewNodeEventHandler;
import regextodfaconverter.directconverter.syntaxtree.node.ScalableInnerNode;
import regextodfaconverter.directconverter.syntaxtree.node.TreeNode;

import utils.Notification;
import utils.Test;


/**
 * 
 * @author Johannes Dahlke
 * 
 */
public class ConcreteSyntaxTree implements Tree, Cloneable {

	private ArrayList<Character> inputCharacters = null;

	private Stack<TreeNode> nodeStack = null;

	public NewNodeEventHandler onNewNodeEvent = null;

	private TreeNode rootNode = null;

	private Grammar grammar;


	public ConcreteSyntaxTree( ContextFreeGrammar grammar, String expression)
			throws Exception {
		this( grammar, expression, null);
	}


	public ConcreteSyntaxTree( ContextFreeGrammar grammar, String expression, NewNodeEventHandler newNodeEventHandler)
			throws Exception {
		this( grammar, expression, newNodeEventHandler, true);
	}

	public ConcreteSyntaxTree( ContextFreeGrammar grammar, String expression, NewNodeEventHandler newNodeEventHandler, Boolean directBuild)
			throws Exception {
		super();
		this.grammar = grammar;
		this.onNewNodeEvent = newNodeEventHandler;
		initTreeSkeleton();
		preprocessInput( expression);
		if ( directBuild)
		  buildTree();
	}
	
	
	protected Stack<TreeNode> getNodeStack() {
		return nodeStack;
	}

	protected void buildTree() {
		ItemAutomata<Character> itemAutomata = new Lr0ItemAutomata<Character>( (ContextFreeGrammar) grammar);

		itemAutomata.setReduceEventHandler( getReduceEventHandler());

		itemAutomata.setShiftEventHandler( getShiftEventHandler());

		itemAutomata.match( inputCharacters);

		rootNode = nodeStack.peek();
	}


	private PrintHandler getNodePrintHandler() {
		return new PrintHandler() {

			public String print( Object... params) {
				String result = "";
				
				for ( Object obj : params) {
					if ( obj instanceof ProductionRule) {
						ProductionRule reduceRule = (ProductionRule) obj;
						if ( !result.isEmpty())
							result += "~";
						result += reduceRule.getLeftRuleSide().toString();
					} else if ( obj instanceof Terminal) {
						if ( !result.isEmpty())
							result += "~";
						result += ((Terminal)obj).toString();
						break;
					}
				}
				return result;
			}
		};
	}


	protected ReduceEventHandler getReduceEventHandler() {
		return new ReduceEventHandler() {

			public Object handle( Object sender, ProductionRule reduceRule) throws Exception {
			
				// create new inner node
				InnerNode<ProductionRule> newInnerNode = new ScalableInnerNode<ProductionRule>( reduceRule);
				newInnerNode.setPrintHandler( getNodePrintHandler());

				if ( Test.isAssigned( onNewNodeEvent))
					onNewNodeEvent.doOnEvent( this, newInnerNode);

				// add childs to the new inner node
				int countOfReducedElements = reduceRule.getRightRuleSide().size();
				for ( int i = 0; i < countOfReducedElements; i++) {
					TreeNode childNode = nodeStack.pop();
					newInnerNode.insertChild( childNode, 0);
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
				Leaf newLeaf = new Leaf( shiftedTerminal);
				newLeaf.setPrintHandler( getNodePrintHandler());

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


	public Iterator<TreeNode> iterator() {
		return new TreeIterator( this);
	}


	public TreeNode getRoot() {
		return rootNode;
	}


	@Override
	public Object clone() throws CloneNotSupportedException {

		ConcreteSyntaxTree clonedTree = (ConcreteSyntaxTree) super.clone();

		clonedTree.grammar = this.grammar;
		clonedTree.rootNode = (TreeNode) this.rootNode.clone();
		clonedTree.onNewNodeEvent = this.onNewNodeEvent;

		return clonedTree;
	}


	public static ConcreteSyntaxTree compress( ConcreteSyntaxTree originalTree) {

		// we work on a copy
		ConcreteSyntaxTree clonedTree;
		try {
			clonedTree = (ConcreteSyntaxTree) originalTree.clone();
		} catch ( CloneNotSupportedException e) {
			Notification.printDebugException( e);
			return null;
		}

		// determine all nodes that can be skipped
		ArrayList<InnerNode> nodesToSkip = new ArrayList<InnerNode>();
		for ( TreeNode treeNode : clonedTree) {
			if ( treeNode instanceof InnerNode) {
				InnerNode innerTreeNode = (InnerNode) treeNode;
				if ( innerTreeNode.childCount() == 1 && Test.isAssigned( innerTreeNode.getParentNode()) && innerTreeNode.getParentNode() instanceof InnerNode) {
					nodesToSkip.add( innerTreeNode);
				}
			}
		}

		// remove singles
		for ( InnerNode innerTreeNode : nodesToSkip) {
			InnerNode innerTreeNodeParent = (InnerNode) innerTreeNode.getParentNode();
			int parentIndex = innerTreeNodeParent.getIndexOf( innerTreeNode);
			TreeNode childNode = innerTreeNode.getNodeWithIndex( 0);
			childNode.insertValues( 0, innerTreeNode.getValues());
			childNode.setParentNode( innerTreeNodeParent, parentIndex);
			innerTreeNode.setParentNode( null);
		}
		return clonedTree;
	}


	@Override
	public String toString() {
		return Test.isAssigned( rootNode) ? ( ( rootNode instanceof InnerNode) ? ( (InnerNode) rootNode).toFullString() : rootNode.toString()) : super.toString();
	}


	public Grammar getGrammar() {
		return grammar;
	}


	public Collection<Leaf> getLeafSet() {
		Collection<Leaf> leafSet = new HashSet<Leaf>();
		for ( TreeNode node : this) {
			if ( Test.isAssigned( node) && node instanceof Leaf) {
				leafSet.add( (Leaf) node); 
			}
		}
		return leafSet;
	}

}
