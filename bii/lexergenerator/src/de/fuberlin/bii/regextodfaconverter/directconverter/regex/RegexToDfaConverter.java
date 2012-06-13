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

package de.fuberlin.bii.regextodfaconverter.directconverter.regex;

import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import de.fuberlin.bii.regextodfaconverter.ConvertExecption;
import de.fuberlin.bii.regextodfaconverter.Regex;
import de.fuberlin.bii.regextodfaconverter.RegexInvalidException;
import de.fuberlin.bii.regextodfaconverter.directconverter.DirectConverterException;
import de.fuberlin.bii.regextodfaconverter.directconverter.PositionToPayloadMap;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.Grammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.ProductionSet;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree.RegexOperatorTree;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree.RegularExpressionElement;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree.TerminalNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.AbstractSyntaxTree;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.AttributesMap;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.ConcreteSyntaxTree;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.SemanticRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.SemanticRules;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.SyntaxDirectedDefinition;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.SyntaxTreeException;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.InnerNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.Leaf;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.NewNodeEventHandler;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNodeCollection;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNodeSet;
import de.fuberlin.bii.regextodfaconverter.fsm.FiniteStateMachine;
import de.fuberlin.bii.regextodfaconverter.fsm.State;
import de.fuberlin.bii.tokenmatcher.StatePayload;
import de.fuberlin.bii.tokenmatcher.attributes.ParseIntAttribute;
import de.fuberlin.bii.tokenmatcher.attributes.ParseStringAttribute;
import de.fuberlin.bii.utils.Notification;
import de.fuberlin.bii.utils.Sets;
import de.fuberlin.bii.utils.Test;


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
	public static FiniteStateMachine<Character, StatePayload> convert( String regex, StatePayload commonPayload)
			throws DirectConverterException {
		PositionToPayloadMap<StatePayload> positionToPayloadMap = new PositionToPayloadMap<StatePayload>();
		return convert( regex, positionToPayloadMap, commonPayload);
	}
	
	public static FiniteStateMachine<Character, StatePayload> convert( RegexToPayloadMap<StatePayload> regexToPayloadMap)
			throws DirectConverterException {
		
		String concatenatedRegex = "";
		PositionToPayloadMap<StatePayload> positionToPayloadMap = new PositionToPayloadMap<StatePayload>();
		
		for ( String regex : regexToPayloadMap.keySet()) {
			if ( !concatenatedRegex.isEmpty())
				concatenatedRegex += "|";
			
			try {
				regex = Regex.reduceAndBracketRegex(regex);
			} catch (RegexInvalidException e) {
				throw new DirectConverterException(
						"Der verwendete reguläre Ausdruck '"
								+ regex
								+ "' ist ungültig oder verwendet nicht unterstütze Operatoren!");
			}
		  concatenatedRegex += regex;
		  positionToPayloadMap.put( concatenatedRegex.length() -1, regexToPayloadMap.get( regex));  
   	}
		return convert( concatenatedRegex, positionToPayloadMap);
	}
	
	public static FiniteStateMachine<Character, StatePayload> convert( String regex, PositionToPayloadMap<StatePayload> positionToPayloadMap)
			throws DirectConverterException {
		return convert( regex, positionToPayloadMap, null);
	}
	
	public static FiniteStateMachine<Character, StatePayload> convert( String regex, PositionToPayloadMap<StatePayload> positionToPayloadMap,  StatePayload commonPayload)
			throws DirectConverterException {
		
		int regexLength = regex.length();
		
		RegularExpressionElement[] regularExpression = new RegularExpressionElement[regexLength];
		for ( int i = 0; i < regexLength; i++) {
			regularExpression[i] = new RegularExpressionElement( regex.charAt( i), positionToPayloadMap.get( i));
		}
		
		return convert( regularExpression, commonPayload);
	}
	
	
	public static FiniteStateMachine<Character, StatePayload> convert( RegularExpressionElement<StatePayload>[] regularExpression, StatePayload commonPayload)
			throws DirectConverterException {
		try {
			RegexOperatorTree regexTree = convertRegexToTree( regularExpression);
			FiniteStateMachine<Character, StatePayload> dfa = convertRegexTreeToDfa( regexTree, commonPayload);
		  return dfa;
		} catch ( Exception e) {
			Notification.printDebugException( e);
			String regexExpression = "";
			for ( RegularExpressionElement<StatePayload> regularExpressionElement : regularExpression) {
				regexExpression += regularExpressionElement.getValue();
			}
			throw new DirectConverterException( String.format( "Cannot convert regex '%s' to DFA.", regularExpression));
		}
	}
	
	

	/**
	 * 
	 * @param Regex
	 * @return
	 * @throws Exception 
	 */
	private static RegexOperatorTree convertRegexToTree( RegularExpressionElement[] regularExpression) throws Exception {
		RegexOperatorTree regexTree = new RegexOperatorTree( regularExpression);
		return regexTree;
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
	private static FiniteStateMachine<Character, StatePayload> convertRegexTreeToDfa( RegexOperatorTree<StatePayload> regexTree, StatePayload commonPayload) throws DirectConverterException {
		try {
			
			HashMap<TreeNodeCollection, State<Character, StatePayload>> unhandledStates = new HashMap<TreeNodeCollection, State<Character, StatePayload>>();

			HashMap<TreeNodeCollection, State<Character, StatePayload>> handledStates = new HashMap<TreeNodeCollection, State<Character, StatePayload>>();

			FiniteStateMachine<Character, StatePayload> dfa = new FiniteStateMachine<Character, StatePayload>();

			// add start state as unhandled
			unhandledStates.put( regexTree.getFirstPositions().get( regexTree.getRoot()), dfa.getInitialState());

			StatePayload currentStatePayload = null;
			
			State<Character, StatePayload> currentState;
			TreeNodeCollection currentCollection;
			while ( !unhandledStates.isEmpty()) {
				// get the next unhandled state ...
				currentCollection = unhandledStates.keySet().iterator().next();
				currentState = unhandledStates.remove( currentCollection);
				dfa.setCurrentState( currentState);
				// ... and mark it as handled
				handledStates.put( currentCollection, currentState);

				HashMap<Character, TreeNodeCollection> stateCandidates = new HashMap<Character, TreeNodeCollection>();
				Character currentTerminalCharacter;

				for ( Leaf leafNode : regexTree.getLeafSet()) {
					RegularExpressionElement<StatePayload> currentRegexElement = (RegularExpressionElement<StatePayload> ) leafNode.getValue();
					TreeNodeCollection followPositionsOfTerminal = new TreeNodeSet();
					for ( TreeNode node : currentCollection) {
						if ( node instanceof TerminalNode) {
							RegularExpressionElement terminalNodeRegexElement = (RegularExpressionElement)((TerminalNode)node).getValue();
						//	System.out.println( terminalNodeRegexElement + " <> " + currentRegexElement);
							if ( terminalNodeRegexElement.equalsTotally( currentRegexElement)) {
								followPositionsOfTerminal.addAll( regexTree.getFollowPositions().get( node));
							}
						}
					}

					// if set not empty, then add set to states
					State<Character, StatePayload> targetState = null;
			//		System.out.println( "followpos EMPTY?: " +currentRegexElement.getValue() + "  " + followPositionsOfTerminal.isEmpty() + "   "  + followPositionsOfTerminal);
					
					if ( !followPositionsOfTerminal.isEmpty()) {
						
						// setze Übergang-spezifischen Payload 
						currentStatePayload = Test.isAssigned( currentRegexElement.getPayload()) ? currentRegexElement.getPayload() : null; 
						// Oder falls keiner definiert, dann den allgemeinen Payload, sofern es sich um das Ende handelt
						if ( Test.isUnassigned( currentStatePayload)
								&& followPositionsOfTerminal.contains( regexTree.getTerminatorNode()))						
							currentStatePayload = commonPayload;
						
						
						if ( Test.isAssigned( currentStatePayload)) {
							// BEGIN: Modification of Algorithm of Glushkov / McNaughton and Yamada 
							// Why? To provide to return more than one payload element (matching more than one word in one dfa)
							targetState = new State<Character, StatePayload>();
							// we do not put it to unhandledStates
							// setze Folgezustand finite
							targetState.setFinite( true);
							// and set payload
							targetState.setPayload( currentStatePayload);
						  // END: Modification of Algorithm of Glushkov / McNaughton and Yamada 
						} else if ( !handledStates.containsKey( followPositionsOfTerminal) && !unhandledStates.containsKey( followPositionsOfTerminal)) {
							targetState = new State<Character, StatePayload>();
							unhandledStates.put( followPositionsOfTerminal, targetState);
						} else if ( handledStates.containsKey( followPositionsOfTerminal)) {
							targetState = handledStates.get( followPositionsOfTerminal);
						} else {
							targetState = unhandledStates.get( followPositionsOfTerminal);
						}

						// setze Übergang
						dfa.addTransition( targetState, currentRegexElement.getValue());
					
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


	
}
