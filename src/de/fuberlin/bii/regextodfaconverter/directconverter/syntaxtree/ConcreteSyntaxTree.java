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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import de.fuberlin.bii.regextodfaconverter.directconverter.AutomatEventHandler;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.ItemAutomat;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.Lr0ItemAutomat;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.ReduceEventHandler;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.Slr1ItemAutomat;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.ShiftEventHandler;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ContextFreeGrammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.EmptyString;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Grammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionSet;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.RegexSpecialChars;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree.OperatorType;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.InnerNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.Leaf;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.NewNodeEventHandler;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.NumberedTreeNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.ScalableInnerNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNode;

import de.fuberlin.bii.utils.Notification;
import de.fuberlin.bii.utils.Test;


/**
 * 
 * @author Johannes Dahlke
 * 
 */
public class ConcreteSyntaxTree<ExpressionElement extends Symbol> implements Tree, Cloneable {

	private ArrayList<ExpressionElement> inputElements = null;

	private Stack<NumberedTreeNode> nodeStack = null;
	
	private HashMap<Integer, Stack> stackSnapshots = new HashMap<Integer, Stack>();

	public NewNodeEventHandler onNewNodeEvent = null;

	private TreeNode rootNode = null;

	private Grammar grammar;


	public ConcreteSyntaxTree( ContextFreeGrammar grammar, ExpressionElement[] expression)
			throws Exception {
		this( grammar, expression, null);
	}


	public ConcreteSyntaxTree( ContextFreeGrammar grammar, ExpressionElement[] expression, NewNodeEventHandler newNodeEventHandler)
			throws Exception {
		this( grammar, expression, newNodeEventHandler, true);
	}

	public ConcreteSyntaxTree( ContextFreeGrammar grammar, ExpressionElement[] expression, NewNodeEventHandler newNodeEventHandler, Boolean directBuild)
			throws Exception {
		super();
		this.grammar = grammar;
		this.onNewNodeEvent = newNodeEventHandler;
		initTreeSkeleton();
		preprocessInput( expression);
		if ( directBuild)
		  buildTree();
	}
	
	
	protected Stack<NumberedTreeNode> getNodeStack() {
		return nodeStack;
	}
	
	protected ItemAutomat<ExpressionElement> getNewItemAutomat( Grammar grammar) {
		return new Slr1ItemAutomat<ExpressionElement>( (ContextFreeGrammar) grammar);
	}

	protected void buildTree() {
		ItemAutomat<ExpressionElement> itemAutomat = getNewItemAutomat( grammar);

		itemAutomat.setReduceEventHandler( getReduceEventHandler());

		itemAutomat.setShiftEventHandler( getShiftEventHandler());
    // System.out.println( "isSLR1 = " + itemAutomat.isReduceConflictFree());
    // System.out.println( itemAutomat);
   	itemAutomat.match( inputElements);
		rootNode = nodeStack.peek().getTreeNode();
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

			public Object handle( Object sender, ProductionRule reduceRule, int sequenceNumber) throws Exception {
				
				updateStackBySequenceNumber( sequenceNumber);
			
				// create new inner node
				InnerNode<ProductionRule> newInnerNode = new ScalableInnerNode<ProductionRule>( reduceRule);
				newInnerNode.setPrintHandler( getNodePrintHandler());

				if ( Test.isAssigned( onNewNodeEvent))
					onNewNodeEvent.doOnEvent( this, newInnerNode);

				// add childs to the new inner node
				int countOfReducedElements = reduceRule.getRightRuleSide().size();
	
				for ( int i = countOfReducedElements; i > 0; i--) {
			  	if ( reduceRule.getRightRuleSide().get( i-1) instanceof EmptyString)
			  		continue;
					// get child from stack
					TreeNode childNode = getNodeStack().pop().getTreeNode();
					// insert child into parent node
					newInnerNode.insertChild( childNode, 0);
				}

				// push the inner node onto stack
				NumberedTreeNode newNumberedNode = new NumberedTreeNode( newInnerNode, sequenceNumber);
				nodeStack.push( newNumberedNode);

				snapshotCurrentStackWithSequenceNumber( sequenceNumber);
				return null;
			}
		};
	}



	protected ShiftEventHandler getShiftEventHandler() {
		return new ShiftEventHandler() {

			public Object handle( Object sender, Terminal shiftedTerminal, int sequenceNumber) throws Exception {
				
				updateStackBySequenceNumber( sequenceNumber);
				
				Leaf newLeaf = new Leaf( shiftedTerminal);
				newLeaf.setPrintHandler( getNodePrintHandler());

				if ( Test.isAssigned( onNewNodeEvent))
					onNewNodeEvent.doOnEvent( this, newLeaf);

				NumberedTreeNode newNumberedLeaf = new NumberedTreeNode( newLeaf, sequenceNumber);
				nodeStack.push( newNumberedLeaf);
				
				snapshotCurrentStackWithSequenceNumber( sequenceNumber);
				return null;
			}
		};
	}


	protected void updateStackBySequenceNumber( int sequenceNumber) {
		// clear overhang
		int recentSerial = -1;

		Set<Integer> keys = new HashSet<Integer>( stackSnapshots.keySet());
		for ( Integer stackSerial : keys) {	
			if ( stackSerial >= sequenceNumber) {
				stackSnapshots.remove( stackSerial);
			}
			else
				recentSerial = Math.max( recentSerial, stackSerial);
		}
		
		// update stack;
		if ( recentSerial > -1) {
		  nodeStack = (Stack) stackSnapshots.get( recentSerial).clone();
		} else {
			nodeStack.clear();
		}
	}
	
	protected void snapshotCurrentStackWithSequenceNumber( int sequenceNumber) {
		stackSnapshots.put( sequenceNumber, (Stack) nodeStack.clone());
	}
	
	
	private void initTreeSkeleton() {
		nodeStack = new Stack<NumberedTreeNode>();
	}


	private void preprocessInput( ExpressionElement[] expression) {
		inputElements = new ArrayList<ExpressionElement>();
		for ( ExpressionElement element : expression) {
			inputElements.add( element);
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
