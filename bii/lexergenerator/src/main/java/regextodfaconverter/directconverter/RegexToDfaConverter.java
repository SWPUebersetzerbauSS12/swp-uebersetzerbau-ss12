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
	 * @param Regex der reguläre Ausdruck in vereinfachter Form.
	 * @param <StatePayloadType> der Inhalt, welcher Zuständen zugeordnet sein kann.
	 * @return ein DFA
	 * @throws Exception 
	 * 
	 */	
	public static <StatePayloadType> FiniteStateMachine<Character, StatePayloadType> convert(String regex) throws Exception {
		try {
		SyntaxTree syntaxTree = convertRegexToSyntaxTree( regex);
		FiniteStateMachine<Character, StatePayloadType> dfa = convertSyntaxTreeToDfa( syntaxTree);
		return dfa;
		} catch( Exception e) {
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
		
		/*
		for ( BinaryTreeNode node : syntaxTree) {
			if ( !( node.nodeValue instanceof Terminal)) {
				System.out.println( node.nodeValue);
			  System.out.println(syntaxTreeAttributor.followpos( node));
			  
			}
		}
		
		for ( BinaryTreeNode binaryTreeNode : syntaxTreeAttributor.followPositions.keySet()) {
			System.out.println(binaryTreeNode.nodeValue);
		  System.out.println(syntaxTreeAttributor.followPositions.get( binaryTreeNode));
			
		}
		*/
		
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
		
		HashMap<State<Character, StatePayloadType>, Collection<BinaryTreeNode>> unhandledStates = new HashMap<State<Character,StatePayloadType>, Collection<BinaryTreeNode>>();
		
		HashMap<Collection<BinaryTreeNode>,State<Character,StatePayloadType>> handledStates = new HashMap<Collection<BinaryTreeNode>,State<Character,StatePayloadType>>();
		
		
		FiniteStateMachine<Character, StatePayloadType> dfa = new FiniteStateMachine<Character, StatePayloadType>();
		
		// add start state as unhandled
		unhandledStates.put( dfa.getCurrentState(), annotations.firstpos( syntaxTree.getRoot()));
		
		State<Character, StatePayloadType>  currentState;
		Collection<BinaryTreeNode> currentCollection;
		while ( !unhandledStates.isEmpty()) {
			currentState = unhandledStates.keySet().iterator().next();
			currentCollection = unhandledStates.get( currentState);
		
			HashMap<Character, Collection<BinaryTreeNode>> stateCandidates = new HashMap<Character, Collection<BinaryTreeNode>>();
			Character currentTerminalCharacter;
			for ( BinaryTreeNode node : currentCollection) {
				assert node.nodeValue instanceof Terminal;
				currentTerminalCharacter = ((Terminal) node.nodeValue).getValue();
				Collection<BinaryTreeNode> union = stateCandidates.get( currentTerminalCharacter);
				union = Sets.unionCollections( union, annotations.followpos( node));
				stateCandidates.put( ((Terminal) node.nodeValue).getValue() , union);
			}
			
			for ( Character terminalCharacter : stateCandidates.keySet()) {
				Collection<BinaryTreeNode> stateCandidate = stateCandidates.get( terminalCharacter);
				
				State<Character, StatePayloadType> targetState;
				if ( !stateCandidate.isEmpty()) { 
					if( !handledStates.containsKey( stateCandidate)) {
					  targetState = new State<Character, StatePayloadType>();
					  handledStates.put( stateCandidate, targetState);
				  } else {
				  	targetState = handledStates.get( stateCandidate);
			  	}
					for ( BinaryTreeNode node : stateCandidate) {
						assert node.nodeValue instanceof Terminal;
						currentTerminalCharacter = ((Terminal) node.nodeValue).getValue();
						dfa.addTransition( targetState, currentTerminalCharacter);			 
					}
				}
				
			}

	  }
		
		return dfa;
	}
		
	
	
}
