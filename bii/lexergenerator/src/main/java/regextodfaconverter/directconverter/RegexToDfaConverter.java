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

import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import regextodfaconverter.ConvertExecption;
import regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import regextodfaconverter.directconverter.lr0parser.grammar.Grammars;
import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionSet;
import regextodfaconverter.directconverter.syntaxtree.SyntaxTree;
import regextodfaconverter.directconverter.syntaxtree.SyntaxTreeAttributor;
import regextodfaconverter.directconverter.syntaxtree.SyntaxTreeException;
import regextodfaconverter.directconverter.syntaxtree.node.BinaryTreeNode;
import regextodfaconverter.directconverter.syntaxtree.node.BinaryTreeNodeCollection;
import regextodfaconverter.directconverter.syntaxtree.node.BinaryTreeNodeSet;
import regextodfaconverter.directconverter.syntaxtree.node.NewNodeEventHandler;
import regextodfaconverter.directconverter.syntaxtree.node.Terminal;
import regextodfaconverter.fsm.FiniteStateMachine;
import regextodfaconverter.fsm.State;
import tokenmatcher.StatePayload;
import utils.Notification;
import utils.Sets;
import utils.Test;


/**
 * Stellt Funktionalitäten bereit, um einen vereinfachten regulären Ausdruck in
 * eine DFA umzuwandeln.
 * 
 * @author Johannes Dahlke
 * 
 * @see <a
 *      href="http://kontext.fraunhofer.de/haenelt/kurs/folien/Haenelt_FSA_RegExFSA.pdf">Fraunhofer
 *      Institut: Überführung regulärer Ausdrücke in endliche Automaten</a>
 * @see <a
 *      href="http://kontext.fraunhofer.de/haenelt/kurs/folien/Haenelt_RegEx-FSA-GMY.pdf">Fraunhofer
 *      Institut: Der Algorithmus von Glushkov und McNaughton/Yamada</a>
 * @see <a
 *      href="http://kontext.fraunhofer.de/haenelt/kurs/folien/FSA-RegA-6.pdf">Endliche
 *      Automaten: Reguläre Mengen, Reguläre Ausdrücke, reguläre Sprachen und
 *      endliche Automaten</a>
 * @see <a
 *      href="http://kontext.fraunhofer.de/haenelt/kurs/Skripten/FSA-Skript/Haenelt_EA_RegEx2EA.pdf">Überführung
 *      regulärer Ausdrücke in endliche Automaten</a>
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
	public static <StatePayloadType extends Serializable> FiniteStateMachine<Character, StatePayloadType> convert( String regex, StatePayloadType payload)
			throws Exception {
		try {
			SyntaxTree syntaxTree = convertRegexToSyntaxTree( regex);
		//TODO	FiniteStateMachine<Character, StatePayloadType> dfa = convertSyntaxTreeToDfa( syntaxTree, payload);
		//	return dfa;
			return null;
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
		SyntaxTree syntaxTree = new SyntaxTree( Grammars.getRegexGrammar(), regex, new NewNodeEventHandler() {

			public void doOnEvent( Object sender, BinaryTreeNode node) {
				syntaxTreeAttributor.nullable( node);
				syntaxTreeAttributor.firstpos( node);
				syntaxTreeAttributor.lastpos( node);
			}
		});

		// calc followpos
		syntaxTreeAttributor.resetFollowPositions();
		for ( BinaryTreeNode node : syntaxTree) {
			syntaxTreeAttributor.followpos( node);
		}
		syntaxTree.setAnnotations( syntaxTreeAttributor);

		return syntaxTree;
	}


	/**
	 * Konvertiert einen annotierten Syntaxbaum in einen deterministischen
	 * endlichen Automaten
	 * 
	 * @param syntaxTree
	 * @return
	 * @throws DirectConverterException
	 * @throws Exception
	 */
	private static <StatePayloadType extends Serializable> FiniteStateMachine<Character, StatePayloadType> convertSyntaxTreeToDfa( SyntaxTree syntaxTree,
			StatePayloadType payload) throws DirectConverterException {
		// ensure, that the syntax tree has annotaions
		if ( Test.isUnassigned( syntaxTree.getAnnotations()))
			throw new DirectConverterException( "Cannot convert syntax tree to DFA. Missing annotations.");

		try {
			SyntaxTreeAttributor annotations = syntaxTree.getAnnotations();

			HashMap<BinaryTreeNodeCollection, State<Character, StatePayloadType>> unhandledStates = new HashMap<BinaryTreeNodeCollection, State<Character, StatePayloadType>>();

			HashMap<BinaryTreeNodeCollection, State<Character, StatePayloadType>> handledStates = new HashMap<BinaryTreeNodeCollection, State<Character, StatePayloadType>>();

			FiniteStateMachine<Character, StatePayloadType> dfa = new FiniteStateMachine<Character, StatePayloadType>();

			// add start state as unhandled
			unhandledStates.put( annotations.firstpos( syntaxTree.getRoot()), dfa.getInitialState());

			State<Character, StatePayloadType> currentState;
			BinaryTreeNodeCollection currentCollection;
			while ( !unhandledStates.isEmpty()) {
				// get the next unhandled state ...
				currentCollection = unhandledStates.keySet().iterator().next();
				currentState = unhandledStates.remove( currentCollection);
				dfa.setCurrentState( currentState);
				// ... and mark it as handled
				handledStates.put( currentCollection, currentState);

				HashMap<Character, BinaryTreeNodeCollection> stateCandidates = new HashMap<Character, BinaryTreeNodeCollection>();
				Character currentTerminalCharacter;

				for ( Character currentCharacterOfCharSet : syntaxTree.getCharacterSet()) {

					BinaryTreeNodeCollection followPositionsOfTerminal = new BinaryTreeNodeSet();
					for ( BinaryTreeNode node : currentCollection) {
						if ( node.nodeValue instanceof Terminal) {
							Character terminalNodeCharacter = ( (Terminal) node.nodeValue).getValue();
							if ( terminalNodeCharacter == currentCharacterOfCharSet) {
								followPositionsOfTerminal.addAll( annotations.followpos( node));
							}
						}
					}

					// if set not empty, then add set to states
					State<Character, StatePayloadType> targetState = null;
					if ( !followPositionsOfTerminal.isEmpty()) {
						if ( !handledStates.containsKey( followPositionsOfTerminal) && !unhandledStates.containsKey( followPositionsOfTerminal)) {
							targetState = new State<Character, StatePayloadType>();
							unhandledStates.put( followPositionsOfTerminal, targetState);
							System.out.println( currentCharacterOfCharSet + " : " + followPositionsOfTerminal);

						} else if ( handledStates.containsKey( followPositionsOfTerminal)) {
							targetState = handledStates.get( followPositionsOfTerminal);
						} else {
							targetState = unhandledStates.get( followPositionsOfTerminal);
						}

						// setze Übergang
						dfa.addTransition( targetState, currentCharacterOfCharSet);
						// und ggf. als Endzustand
						if ( followPositionsOfTerminal.contains( syntaxTree.getTerminatorNode())) {
							targetState.setFinite( true);
							targetState.setPayload( payload);
						}

					}

				}

			}

			assert dfa.isDeterministic();

			return dfa;

		} catch ( Exception e) {
			Notification.printDebugException( e);
			throw new DirectConverterException( "Cannot convert syntax tree to DFA. " + e.getMessage());
		}

	}


	private static <StatePayloadType extends Serializable> FiniteStateMachine<Character, StatePayloadType> unifyDfa(
			FiniteStateMachine<Character, StatePayloadType> destinationMachine, FiniteStateMachine<Serializable, Serializable>... sourceMachines) throws Exception {
		// ensure, that the syntax tree has annotaions
		if ( Test.isUnassigned( sourceMachines))
			return destinationMachine;
		else if ( Test.isUnassigned( destinationMachine)) {
			throw new ConvertExecption( "Cannot unify the deterministic automats, cause there is no destination automata specified.");
		} else if ( !destinationMachine.isDeterministic()) {
			throw new ConvertExecption( "Cannot unify the deterministic automats. The destination automata is not deterministic.");
		} else {
			for ( FiniteStateMachine<Serializable, Serializable> sourceMachine : sourceMachines) {
				if ( !sourceMachine.isDeterministic()) {
					throw new ConvertExecption( "Cannot unify the deterministic automats. One of the source automats is not deterministic.");
				}

				// now we can start to merge the states of the automats
				/*
				 * for ( FiniteStateMachine<Serializable, Serializable> sourceMachine :
				 * sourceMachines) { sourceMachine.getCurrentState(). }
				 */
			}

		}
		return destinationMachine;
	}
}
