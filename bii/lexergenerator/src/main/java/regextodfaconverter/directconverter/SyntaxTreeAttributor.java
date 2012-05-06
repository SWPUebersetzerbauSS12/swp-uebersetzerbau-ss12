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
import java.util.HashMap;


/**
 * 
 * @author Johannes Dahlke
 *
 */
public class SyntaxTreeAttributor {
	
	private HashMap<BinaryTreeNode,Collection<BinaryTreeNode>> followPositions = new HashMap<BinaryTreeNode, Collection<BinaryTreeNode>>();
	private HashMap<BinaryTreeNode,Collection<BinaryTreeNode>> lastPositions = new HashMap<BinaryTreeNode, Collection<BinaryTreeNode>>();
	private HashMap<BinaryTreeNode,Collection<BinaryTreeNode>> firstPositions = new HashMap<BinaryTreeNode, Collection<BinaryTreeNode>>();
	private HashMap<BinaryTreeNode,Boolean> nullables = new HashMap<BinaryTreeNode, Boolean>();
	

	

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


	private Collection<BinaryTreeNode> calculateFirstposForNode( BinaryTreeNode node) {
		// \epsilon-Knoten liefern per definition die leere Menge
		if ( node.nodeValue instanceof Terminal) {
			if ( ( (Terminal) node.nodeValue).getValue() == RegexSpecialChars.EMPTY_STRING)
				return new ArrayList<BinaryTreeNode>();
			else { // Terminale != \epsilonliefern das aktuelle Element
				Collection<BinaryTreeNode> result = new ArrayList<BinaryTreeNode>();
				result.add( node);
				return result;
			}
		} else { // der Knoten enthält eine Operation
			Operator operator = (Operator) node.nodeValue;
			switch ( operator.getOperatorType()) {
				case ALTERNATIVE: {// Vereinigung der firstpos-Mengen
					Collection<BinaryTreeNode> result = firstpos( node.leftChildNode);
					result.addAll( firstpos( node.rightChildNode));
					return result;
				}
				case CONCATENATION:
					if ( nullable( node.leftChildNode)) {
						Collection<BinaryTreeNode> result = firstpos( node.leftChildNode);
						result.addAll( firstpos( node.rightChildNode));
						return result;
					} else {
						return firstpos( node.leftChildNode);
					}
				default: // REPETITION
					return firstpos( node.leftChildNode);
			}
		}
	}


	
	private Collection<BinaryTreeNode> calculateLastposForNode( BinaryTreeNode node) {
		// \epsilon-Knoten liefern per definition die leere Menge
		if ( node.nodeValue instanceof Terminal) {
			if ( ( (Terminal) node.nodeValue).getValue() == RegexSpecialChars.EMPTY_STRING)
				return new ArrayList<BinaryTreeNode>();
			else { // Terminale != \epsilon liefern das aktuelle Element
				Collection<BinaryTreeNode> result = new ArrayList<BinaryTreeNode>();
				result.add( node);
				return result;
			}
		} else { // der Knoten enthält eine Operation
			Operator operator = (Operator) node.nodeValue;
			switch ( operator.getOperatorType()) {
				case ALTERNATIVE: {// Vereinigung der lastpos-Mengen
					Collection<BinaryTreeNode> result = lastpos( node.leftChildNode);
					result.addAll( firstpos( node.rightChildNode));
					return result;
				}
				case CONCATENATION:
					if ( nullable( node.leftChildNode)) {
						Collection<BinaryTreeNode> result = lastpos( node.leftChildNode);
						result.addAll( lastpos( node.rightChildNode));
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
		if ( node.nodeValue instanceof Terminal) {
		  throw new SyntaxTreeException( "There is no definition to calculate the folowpos on a terminal");
		} else { // der Knoten enthält eine Operation
			Operator operator = (Operator) node.nodeValue;
			switch ( operator.getOperatorType()) {
				case ALTERNATIVE: 
					// keine Reihenfolge -> nichts zu tun
				case CONCATENATION:
					for ( BinaryTreeNode lastposNode  : lastpos( node.leftChildNode)) {
						Collection<BinaryTreeNode> union = followPositions.get( lastposNode);
						union.addAll( firstpos( node.rightChildNode));
						followPositions.put( lastposNode, union);
					}
				default: // REPETITION
					for ( BinaryTreeNode lastposNode  : lastpos( node)) {
						Collection<BinaryTreeNode> union = followPositions.get( lastposNode);
						union.addAll( firstpos( node));
						followPositions.put( lastposNode, union);
					}
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
	public Collection<BinaryTreeNode> firstpos( BinaryTreeNode node) {
		
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
	public Collection<BinaryTreeNode> lastpos( BinaryTreeNode node) {	
		
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
	public Collection<BinaryTreeNode> followpos( BinaryTreeNode node) throws SyntaxTreeException {
		calculateFollowposForNode( node);
		return followPositions.get( node);
	}
	
	
	public void resetFollowPositions() {
		followPositions.clear();
	}
}
