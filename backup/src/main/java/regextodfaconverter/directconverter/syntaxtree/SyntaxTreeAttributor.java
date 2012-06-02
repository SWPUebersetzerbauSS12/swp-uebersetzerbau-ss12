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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import regextodfaconverter.directconverter.RegexSpecialChars;
import regextodfaconverter.directconverter.syntaxtree.node.BinaryTreeNode;
import regextodfaconverter.directconverter.syntaxtree.node.BinaryTreeNodeCollection;
import regextodfaconverter.directconverter.syntaxtree.node.BinaryTreeNodeSet;
import regextodfaconverter.directconverter.syntaxtree.node.Operator;
import regextodfaconverter.directconverter.syntaxtree.node.Terminal;

import utils.Sets;
import utils.Test;


/**
 * 
 * @author Johannes Dahlke
 *
 */
public class SyntaxTreeAttributor {
	
	public HashMap<BinaryTreeNode,BinaryTreeNodeCollection> followPositions = new HashMap<BinaryTreeNode, BinaryTreeNodeCollection>();
	public HashMap<BinaryTreeNode,BinaryTreeNodeCollection> lastPositions = new HashMap<BinaryTreeNode, BinaryTreeNodeCollection>();
	public HashMap<BinaryTreeNode,BinaryTreeNodeCollection> firstPositions = new HashMap<BinaryTreeNode, BinaryTreeNodeCollection>();
	public HashMap<BinaryTreeNode,Boolean> nullables = new HashMap<BinaryTreeNode, Boolean>();
	

	
	private boolean calculateNullableForNode( BinaryTreeNode node) {
		// \epsilon-Knoten sind per definition true
		if ( node.nodeValue instanceof Terminal) {
			if ( ( (Terminal) node.nodeValue).getValue() == RegexSpecialChars.EMPTY_STRING)
				return true;
			else
				// Terminale != \epsilon sind nicht nullable
				return false;
		} else { // der Knoten enthält eine Operation
			Operator operator = (Operator) node.nodeValue;
			switch ( operator.getOperatorType()) {
				case ALTERNATIVE:
					return nullable( node.leftChildNode)
							|| nullable( node.rightChildNode);
				case CONCATENATION:
					return nullable( node.leftChildNode)
							&& nullable( node.rightChildNode);
				default: // REPETITION
					return true;
			}
		}
	}


	private BinaryTreeNodeCollection calculateFirstposForNode( BinaryTreeNode node) {
		// \epsilon-Knoten liefern per definition die leere Menge
		if ( node.nodeValue instanceof Terminal) {
			if ( ( (Terminal) node.nodeValue).getValue() == RegexSpecialChars.EMPTY_STRING)
				return new BinaryTreeNodeSet();
			else { // Terminale != \epsilon liefern das aktuelle Element
				BinaryTreeNodeCollection result = new BinaryTreeNodeSet();
				result.add( node);
				return result;
			}
		} else { // der Knoten enthält eine Operation
			Operator operator = (Operator) node.nodeValue;
			switch ( operator.getOperatorType()) {
				case ALTERNATIVE: {// Vereinigung der firstpos-Mengen
					BinaryTreeNodeCollection result = firstpos( node.leftChildNode);
					result = Sets.unionCollections( result, firstpos( node.rightChildNode));
					return result;
				}
				case CONCATENATION:
					if ( nullable( node.leftChildNode)) {
						BinaryTreeNodeCollection result = firstpos( node.leftChildNode);
						result = Sets.unionCollections( result, firstpos( node.rightChildNode));
						return result;
					} else {
						return firstpos( node.leftChildNode);
					}
				default: // REPETITION
					return firstpos( node.leftChildNode);
			}
		}
	}


	
	private BinaryTreeNodeCollection calculateLastposForNode( BinaryTreeNode node) {
		// \epsilon-Knoten liefern per definition die leere Menge
		if ( node.nodeValue instanceof Terminal) {
			if ( ( (Terminal) node.nodeValue).getValue() == RegexSpecialChars.EMPTY_STRING)
				return new BinaryTreeNodeSet();
			else { // Terminale != \epsilon liefern das aktuelle Element
				BinaryTreeNodeCollection result = new BinaryTreeNodeSet();
				result.add( node);
				return result;
			}
		} else { // der Knoten enthält eine Operation
			Operator operator = (Operator) node.nodeValue;
			switch ( operator.getOperatorType()) {
				case ALTERNATIVE: {// Vereinigung der lastpos-Mengen
					BinaryTreeNodeCollection result = lastpos( node.leftChildNode);
					result = Sets.unionCollections( result, firstpos( node.rightChildNode));
					return result;
				}
				case CONCATENATION:
					if ( nullable( node.rightChildNode)) {
						BinaryTreeNodeCollection result = lastpos( node.leftChildNode);
						result = Sets.unionCollections( result, lastpos( node.rightChildNode));
						return result;
					} else {
						return lastpos( node.rightChildNode);
					}
				default: // REPETITION
					return firstpos( node.leftChildNode);
			}
		}
	}
	
	
	private void calculateFollowposForNode( BinaryTreeNode node) throws SyntaxTreeException {
		// init to prevent null items in followPositions
		if ( Test.isUnassigned(  followPositions.get( node))) {
			followPositions.put( node, new BinaryTreeNodeSet());
		}
			
		if ( node.nodeValue instanceof Terminal) {
		  // leere Menge -> nichts zu tun
		} else { // der Knoten enthält eine Operation
			Operator operator = (Operator) node.nodeValue;
			switch ( operator.getOperatorType()) {
		  	case CONCATENATION: {
		  		BinaryTreeNodeCollection lastpos = lastpos( node.leftChildNode);
					for ( BinaryTreeNode lastposNode  : lastpos) {
						BinaryTreeNodeCollection union = followPositions.remove( lastposNode);
						union = Sets.unionCollections( union, firstpos( node.rightChildNode));
						followPositions.put( lastposNode, union);
					}
				}
		  	break;
		  	case REPETITION: {
		  		BinaryTreeNodeCollection lastpos = lastpos( node);
					for ( BinaryTreeNode lastposNode  : lastpos) {
						BinaryTreeNodeCollection union = followPositions.remove( lastposNode);
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
	public boolean nullable( BinaryTreeNode node) {
		
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
	public BinaryTreeNodeCollection firstpos( BinaryTreeNode node) {
		
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
	public BinaryTreeNodeCollection lastpos( BinaryTreeNode node) {	
		
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
	public BinaryTreeNodeCollection followpos( BinaryTreeNode node) throws SyntaxTreeException {
			calculateFollowposForNode( node);
			return followPositions.get( node);
	}
		
	public void resetFollowPositions() {
		followPositions.clear();
	}
}
