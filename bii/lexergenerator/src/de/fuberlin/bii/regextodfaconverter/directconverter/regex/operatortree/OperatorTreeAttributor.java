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

package regextodfaconverter.directconverter.regex.operatortree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import regextodfaconverter.directconverter.lr0parser.grammar.Symbol;
import regextodfaconverter.directconverter.regex.RegexSpecialChars;
import regextodfaconverter.directconverter.syntaxtree.SyntaxTreeException;
import regextodfaconverter.directconverter.syntaxtree.node.TreeNode;
import regextodfaconverter.directconverter.syntaxtree.node.TreeNode;
import regextodfaconverter.directconverter.syntaxtree.node.TreeNodeCollection;
import regextodfaconverter.directconverter.syntaxtree.node.TreeNodeSet;
import regextodfaconverter.directconverter.syntaxtree.node.Leaf;
import utils.Sets;
import utils.Test;


/**
 * 
 * @author Johannes Dahlke
 *
 */
public class OperatorTreeAttributor<StatePayloadType extends Serializable>  {
	
	private HashMap<TreeNode,TreeNodeCollection> followPositions = new HashMap<TreeNode, TreeNodeCollection>();
	private HashMap<TreeNode,TreeNodeCollection> lastPositions = new HashMap<TreeNode, TreeNodeCollection>();
	private HashMap<TreeNode,TreeNodeCollection> firstPositions = new HashMap<TreeNode, TreeNodeCollection>();
	private HashMap<TreeNode,Boolean> nullables = new HashMap<TreeNode, Boolean>();
	

	
	private boolean calculateNullableForNode( TreeNode node) {
		if ( node instanceof TerminalNode) {
		  // \epsilon-Knoten sind per definition true
			RegularExpressionElement<StatePayloadType> regexElement = (RegularExpressionElement<StatePayloadType>)((TerminalNode)node).getValue();
			if ( regexElement.getValue() == RegexSpecialChars.EMPTY_STRING)
				return true;
			else
				// Terminale != \epsilon sind nicht nullable
				return false;
		} else { // der Knoten enthält eine Operation
			OperatorNode operatorNode = (OperatorNode) node;
			switch ( operatorNode.getOperatorType()) {
				case ALTERNATIVE:
					return nullable( operatorNode.getLeftChildNode())
							|| nullable( operatorNode.getRightChildNode());
				case CONCATENATION:
					return nullable( operatorNode.getLeftChildNode())
							&& nullable( operatorNode.getRightChildNode());
				default: // REPETITION
					return true;
			}
		}
	}


	private TreeNodeCollection calculateFirstposForNode( TreeNode node) {
		// \epsilon-Knoten liefern per definition die leere Menge
		if ( node instanceof TerminalNode) {
			RegularExpressionElement<StatePayloadType>  regexElement = (RegularExpressionElement<StatePayloadType>)((TerminalNode)node).getValue();
			if ( regexElement.getValue() == RegexSpecialChars.EMPTY_STRING)
				return new TreeNodeSet();
			else { // Terminale != \epsilon liefern das aktuelle Element
				TreeNodeCollection result = new TreeNodeSet();
				result.add( node);
				return result;
			}
		} else { // der Knoten enthält eine Operation
			OperatorNode operatorNode = (OperatorNode) node;
			switch ( operatorNode.getOperatorType()) {
				case ALTERNATIVE: {// Vereinigung der firstpos-Mengen
					TreeNodeCollection result = firstpos( operatorNode.getLeftChildNode());
					result = Sets.unionCollections( result, firstpos( operatorNode.getRightChildNode()));
					return result;
				}
				case CONCATENATION:
					if ( nullable( operatorNode.getLeftChildNode())) {
						TreeNodeCollection result = firstpos( operatorNode.getLeftChildNode());
						result = Sets.unionCollections( result, firstpos( operatorNode.getRightChildNode()));
						return result;
					} else {
						return firstpos( operatorNode.getLeftChildNode());
					}
				default: // REPETITION
					return firstpos( operatorNode.getLeftChildNode());
			}
		}
	}


	
	private TreeNodeCollection calculateLastposForNode( TreeNode node) {
		// \epsilon-Knoten liefern per definition die leere Menge
		if ( node instanceof TerminalNode) {
			RegularExpressionElement<StatePayloadType> regexElement = (RegularExpressionElement<StatePayloadType>)((TerminalNode)node).getValue();
			if ( regexElement.getValue() == RegexSpecialChars.EMPTY_STRING)
				return new TreeNodeSet();
			else { // Terminale != \epsilon liefern das aktuelle Element
				TreeNodeCollection result = new TreeNodeSet();
				result.add( node);
				return result;
			}
		} else { // der Knoten enthält eine Operation
			OperatorNode operatorNode = (OperatorNode) node;
			switch ( operatorNode.getOperatorType()) {
				case ALTERNATIVE: {// Vereinigung der lastpos-Mengen
					TreeNodeCollection result = lastpos( operatorNode.getLeftChildNode());
					result = Sets.unionCollections( result, lastpos( operatorNode.getRightChildNode()));
					return result;
				}
				case CONCATENATION:
					if ( nullable( operatorNode.getRightChildNode())) {
						TreeNodeCollection result = lastpos( operatorNode.getLeftChildNode());
						result = Sets.unionCollections( result, lastpos( operatorNode.getRightChildNode()));
						return result;
					} else {
						return lastpos( operatorNode.getRightChildNode());
					}
				default: // REPETITION
					return lastpos( operatorNode.getLeftChildNode());
			}
		}
	}
	
	
	private void calculateFollowposForNode( TreeNode node)  {
		// init to prevent null items in followPositions
		if ( Test.isUnassigned(  followPositions.get( node))) {
			followPositions.put( node, new TreeNodeSet());
		}
			
		if ( node instanceof TerminalNode) {
		  // leere Menge -> nichts zu tun
		} else { // der Knoten enthält eine Operation
			OperatorNode operatorNode = (OperatorNode) node;
			switch ( operatorNode.getOperatorType()) {
		  	case CONCATENATION: {
		  		TreeNodeCollection lastpos = lastpos( operatorNode.getLeftChildNode());
					for ( TreeNode lastposNode  : lastpos) {
						TreeNodeCollection union = followPositions.remove( lastposNode);
						union = Sets.unionCollections( union, firstpos( operatorNode.getRightChildNode()));
						followPositions.put( lastposNode, union);
					}
				}
		  	break;
		  	case REPETITION: {
		  		TreeNodeCollection lastpos = lastpos( node);
					for ( TreeNode lastposNode  : lastpos) {
						TreeNodeCollection union = followPositions.remove( lastposNode);
						union = Sets.unionCollections(  union, firstpos( node));
						followPositions.put( lastposNode, union);
					}
				}
		  	break;
				case ALTERNATIVE: 
				default:	
					// keine Reihenfolge -> nichts zu tun
			}
		}
	}
	
	
	
	
	/**
	 * Prüft, ob der Teilbaum das Lesen des leere Wort ermöglicht.
	 * 
	 * @param node
	 * @return
   * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/Skripten/FSA-Skript/Haenelt_EA_RegEx2EA.pdf">Überführung regulärer Ausdrücke in endliche Automaten (S. 24)</a>
	 */
	private boolean nullable( TreeNode node) {
		
		if ( !nullables.containsKey( node))
			nullables.put( node, calculateNullableForNode( node));
		
		return nullables.get( node);
	}


	/**
	 * Liefert eine Sammlung aller Knoten, die bei Worten gebildet über den
	 * Unterbaum ab diesem Knoten an erste Stelle stehen können.
	 * 
	 * @param node
	 * @return
   * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/Skripten/FSA-Skript/Haenelt_EA_RegEx2EA.pdf">Überführung regulärer Ausdrücke in endliche Automaten (S. 25)</a>
	 */
	private TreeNodeCollection firstpos( TreeNode node) {
		
		if ( !firstPositions.containsKey( node))
			firstPositions.put( node, calculateFirstposForNode( node));
		
		return firstPositions.get( node);
	}
	
	
	
	
	/**
	 * Liefert eine Sammlung aller Knoten, die am Ende eines Wortes stehen können,
	 * welches über den Unterbaum den Knoten n gebildet werden können.
	 * 
	 * @param node
	 * @return
   * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/Skripten/FSA-Skript/Haenelt_EA_RegEx2EA.pdf">Überführung regulärer Ausdrücke in endliche Automaten (S. 25)</a>
	 */
	private TreeNodeCollection lastpos( TreeNode node) {	
		
		if ( !lastPositions.containsKey( node))
			lastPositions.put( node, calculateLastposForNode( node));
		
		return lastPositions.get( node);
	}
	
	
	
	/**
	 * Liefert eine Sammlung aller Knoten, die auf den gegebenen Knoten in einem Wort folgen können.
	 * 
	 * @param node
	 * @return
	 * @throws SyntaxTreeException 
   * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/Skripten/FSA-Skript/Haenelt_EA_RegEx2EA.pdf">Überführung regulärer Ausdrücke in endliche Automaten (S. 27)</a>
	 */
	private TreeNodeCollection followpos( TreeNode node)  {
			calculateFollowposForNode( node);
			return followPositions.get( node);
	}
		
	private void resetFollowPositions() {
		followPositions.clear();
	}
	
	
	
	public HashMap<TreeNode, TreeNodeCollection> getFirstPositions() {
		return firstPositions;
	}
	
	
	public HashMap<TreeNode, TreeNodeCollection> getFollowPositions() {
		return followPositions;
	}
	
	
	public HashMap<TreeNode, TreeNodeCollection> getLastPositions() {
		return lastPositions;
	}
	
	
	public HashMap<TreeNode, Boolean> getNullables() {
		return nullables;
	}
	
	public void attributizeOperatorTree( RegexOperatorTree operatorTree) {
		for ( TreeNode treeNode : operatorTree) {
			if ( treeNode instanceof TerminalNode  // filter empty dummy nodes
					|| treeNode instanceof OperatorNode) {
			  nullable( treeNode);
			  firstpos( treeNode);
			  lastpos( treeNode);
			}
		}	
   	// calc followpos
		resetFollowPositions();
		for ( TreeNode treeNode : operatorTree) {
			if ( treeNode instanceof TerminalNode
					|| treeNode instanceof OperatorNode) {
			  followpos( treeNode);
			}
		}

		
	}

}
