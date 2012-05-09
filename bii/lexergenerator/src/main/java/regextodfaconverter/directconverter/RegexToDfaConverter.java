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

import regextodfaconverter.directconverter.syntaxtree.SyntaxTree;
import regextodfaconverter.directconverter.syntaxtree.SyntaxTreeAttributor;
import regextodfaconverter.directconverter.syntaxtree.SyntaxTreeException;
import regextodfaconverter.directconverter.syntaxtree.node.BinaryTreeNode;
import regextodfaconverter.directconverter.syntaxtree.node.BinaryTreeNodeCollection;
import regextodfaconverter.directconverter.syntaxtree.node.NewNodeEventHandler;
import regextodfaconverter.directconverter.syntaxtree.node.Terminal;
import regextodfaconverter.fsm.FiniteStateMachine;
import regextodfaconverter.fsm.State;
import tokenmatcher.StatePayload;
import utils.Notification;
import utils.Sets;
import utils.Test;

/**
 * Stellt Funktionalitäten bereit, um einen vereinfachten regulären Ausdruck in eine DFA umzuwandeln. 
 * 
 * @author Johannes Dahlke
 * 
 * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/folien/Haenelt_FSA_RegExFSA.pdf">Fraunhofer Institut: Überführung regulärer Ausdrücke in endliche Automaten</a>
 * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/folien/Haenelt_RegEx-FSA-GMY.pdf">Fraunhofer Institut: Der Algorithmus von Glushkov und McNaughton/Yamada</a>
 * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/folien/FSA-RegA-6.pdf">Endliche Automaten: Reguläre Mengen, Reguläre Ausdrücke, reguläre Sprachen und endliche Automaten</a>
 * @see <a href="http://kontext.fraunhofer.de/haenelt/kurs/Skripten/FSA-Skript/Haenelt_EA_RegEx2EA.pdf">Überführung regulärer Ausdrücke in endliche Automaten</a>
 */
public class RegexToDfaConverter {
	

	/**
	 * Wandelt einen vereinfachten regulären Ausdruck in einen DFA um.
	 * 
	 * @param Regex
	 *          der reguläre Ausdruck in vereinfachter Form.
	 * @param <StatePayloadType>
	 *          der Inhalt, welcher Zuständen zugeordnet sein kann.
	 * @return ein DFA
	 * @throws Exception
	 * 
	 */
	public static <StatePayloadType> FiniteStateMachine<Character, StatePayloadType> convert(
			String regex) throws Exception {
		try {
			String terminizedRegex = "(" + regex + ")" + RegexSpecialChars.TERMINATOR;
			SyntaxTree syntaxTree = convertRegexToSyntaxTree( terminizedRegex);
			FiniteStateMachine<Character, StatePayloadType> dfa = convertSyntaxTreeToDfa( syntaxTree);
			return dfa;
		} catch ( Exception e) {
			Notification.printDebugException( e);
			throw new Exception( "Cannot convert regex to DFA.");
		}
	}


  /**
   * 
   * @param Regex
   * @return
   * @throws SyntaxTreeException 
   */
	private static SyntaxTree convertRegexToSyntaxTree( String regex) throws SyntaxTreeException {
		final SyntaxTreeAttributor syntaxTreeAttributor = new SyntaxTreeAttributor();
		SyntaxTree syntaxTree = new SyntaxTree( regex, new NewNodeEventHandler() {
			
			public void doOnEvent( Object sender, BinaryTreeNode node) {
				syntaxTreeAttributor.nullable( node);
				syntaxTreeAttributor.firstpos( node);
				syntaxTreeAttributor.lastpos( node);
			}
		} );
		
		
		// calc followpos
		syntaxTreeAttributor.resetFollowPositions();
		for ( BinaryTreeNode node : syntaxTree) {
			syntaxTreeAttributor.followpos( node);
		}
		syntaxTree.setAnnotations( syntaxTreeAttributor);
	
		return syntaxTree;
	}
	
	/**
	 * Konvertiert einen annotierten 
	 * @param syntaxTree
	 * @return
	 * @throws Exception 
	 */
	private static <StatePayloadType> FiniteStateMachine<Character, StatePayloadType> convertSyntaxTreeToDfa( SyntaxTree syntaxTree) throws Exception {
		// ensure, that the syntax tree has annotaions
		if ( Test.isUnassigned( syntaxTree.getAnnotations()))
			throw new Exception( "Cannot convert syntax tree to DFA. Missing annotations.");
		
		SyntaxTreeAttributor annotations = syntaxTree.getAnnotations();
		
		HashMap<State<Character, StatePayloadType>, BinaryTreeNodeCollection> unhandledStates = new HashMap<State<Character,StatePayloadType>, BinaryTreeNodeCollection>();
		
		HashMap<BinaryTreeNodeCollection,State<Character,StatePayloadType>> handledStates = new HashMap<BinaryTreeNodeCollection,State<Character,StatePayloadType>>();
		
		
		FiniteStateMachine<Character, StatePayloadType> dfa = new FiniteStateMachine<Character, StatePayloadType>();
		
		// add start state as unhandled
		unhandledStates.put( dfa.getCurrentState(), annotations.firstpos( syntaxTree.getRoot()));
		
		State<Character, StatePayloadType>  currentState;
		BinaryTreeNodeCollection currentCollection;
		while ( !unhandledStates.isEmpty()) {
			// get the next unhandled state ...
			currentState = unhandledStates.keySet().iterator().next();
			currentCollection = unhandledStates.get( currentState);
			// ... and mark it as handled
			handledStates.put( unhandledStates.remove( currentState), currentState);
		
			HashMap<Character, BinaryTreeNodeCollection> stateCandidates = new HashMap<Character, BinaryTreeNodeCollection>();
			Character currentTerminalCharacter;
			for ( BinaryTreeNode node : currentCollection) {
				assert node.nodeValue instanceof Terminal;
				currentTerminalCharacter = ((Terminal) node.nodeValue).getValue();
				BinaryTreeNodeCollection union = stateCandidates.remove( currentTerminalCharacter);
				union = (BinaryTreeNodeCollection) Sets.unionCollections( union, annotations.followpos( node));
				stateCandidates.put( currentTerminalCharacter, union);
			}
			for ( Character terminalCharacter : stateCandidates.keySet()) {
				BinaryTreeNodeCollection stateCandidate = stateCandidates.get( terminalCharacter);
				State<Character, StatePayloadType> targetState;
				if ( !stateCandidate.isEmpty()) { 
					if( !handledStates.containsKey( stateCandidate)) {
					  targetState = new State<Character, StatePayloadType>();
					  unhandledStates.put( targetState, stateCandidate);
				  } else {
				  	targetState = handledStates.get( stateCandidate);
			  	}
					for ( BinaryTreeNode node : stateCandidate) {
					//	System.out.println(node);
						assert node.nodeValue instanceof Terminal;
						currentTerminalCharacter = ((Terminal) node.nodeValue).getValue();
						dfa.addTransition( targetState, currentTerminalCharacter);		
						if ( currentTerminalCharacter == RegexSpecialChars.TERMINATOR) {
							targetState.setFinite( true);
						}
					}
				}
				
			}
			for ( BinaryTreeNodeCollection binaryTreeNode : handledStates.keySet()) {
				System.out.println( binaryTreeNode);
				
			}

	  }
		
		return dfa;
	}
		
	
	
}
