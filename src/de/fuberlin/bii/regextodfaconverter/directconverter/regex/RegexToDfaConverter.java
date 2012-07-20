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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.fuberlin.bii.regextodfaconverter.Regex;
import de.fuberlin.bii.regextodfaconverter.RegexInvalidException;
import de.fuberlin.bii.regextodfaconverter.directconverter.DirectConverterException;
import de.fuberlin.bii.regextodfaconverter.directconverter.PositionToPayloadMap;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree.RegexOperatorTree;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree.RegularExpressionElement;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree.TerminalNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.Leaf;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNodeCollection;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNodeSet;
import de.fuberlin.bii.regextodfaconverter.fsm.FiniteStateMachine;
import de.fuberlin.bii.regextodfaconverter.fsm.State;
import de.fuberlin.bii.regextodfaconverter.fsm.StatePayload;
import de.fuberlin.bii.regextodfaconverter.fsm.Transition;
import de.fuberlin.bii.utils.Notification;
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
@SuppressWarnings("rawtypes")
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
	public static FiniteStateMachine<Character, ? extends de.fuberlin.bii.tokenmatcher.StatePayload> convert( String regex, StatePayload commonPayload)
			throws DirectConverterException {
		PositionToPayloadMap<StatePayload> positionToPayloadMap = new PositionToPayloadMap<StatePayload>();
		return convert( regex, positionToPayloadMap, commonPayload);
	}
	
	public static FiniteStateMachine<Character, ? extends de.fuberlin.bii.tokenmatcher.StatePayload> convert( RegexToPayloadMap<StatePayload> regexToPayloadMap)
			throws DirectConverterException {
		String concatenatedRegex = "";
		PositionToPayloadMap<StatePayload> positionToPayloadMap = new PositionToPayloadMap<StatePayload>();
		
		for ( String regex : regexToPayloadMap.keySet()) {
			if ( !concatenatedRegex.isEmpty())
				concatenatedRegex += "|";
		  concatenatedRegex += "(" + regex +")";
		  positionToPayloadMap.put( concatenatedRegex.length() -1, regexToPayloadMap.get( regex));  
   	}
		return convert( concatenatedRegex, positionToPayloadMap);
	}
	
	public static FiniteStateMachine<Character, ? extends de.fuberlin.bii.tokenmatcher.StatePayload> convert( String regex, PositionToPayloadMap<StatePayload> positionToPayloadMap)
			throws DirectConverterException {
		return convert( regex, positionToPayloadMap, null);
	}
	
	public static FiniteStateMachine<Character, ? extends de.fuberlin.bii.tokenmatcher.StatePayload> convert( String regex, PositionToPayloadMap<StatePayload> positionToPayloadMap,  StatePayload commonPayload)
			throws DirectConverterException {
		
		int regexLength = regex.length();
		
		@SuppressWarnings("unchecked")
		RegularExpressionElement<StatePayload>[] regularExpression = new RegularExpressionElement[regexLength];
		for ( int i = 0; i < regexLength; i++) {
			regularExpression[i] = new RegularExpressionElement<StatePayload>( regex.charAt( i), positionToPayloadMap.get( i));
		}
		
		return convert( regularExpression, commonPayload);
	}
	
	
	public static FiniteStateMachine<Character, ? extends de.fuberlin.bii.tokenmatcher.StatePayload> convert( RegularExpressionElement<StatePayload>[] regularExpression, StatePayload commonPayload)
			throws DirectConverterException {
		try {
			RegexOperatorTree<StatePayload> regexTree = convertRegexToTree( regularExpression);
			if ( Test.isUnassigned(  commonPayload))
        commonPayload = getWeakestPayload( regularExpression);
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
	
	private static StatePayload getWeakestPayload( RegularExpressionElement<StatePayload>[] regularExpression) {
		StatePayload weakestPayload = null;
		for ( RegularExpressionElement<StatePayload> regularExpressionElement : regularExpression) {
			if ( Test.isUnassigned( regularExpressionElement))
				continue;
      if ( Test.isAssigned( weakestPayload)) {
      	if ( Test.isAssigned( regularExpressionElement.getPayload())) {
          if ( weakestPayload.getPriority() > regularExpressionElement.getPayload().getPriority())
          	weakestPayload = regularExpressionElement.getPayload();
      	}
      } else {
      	weakestPayload = regularExpressionElement.getPayload();	
      }
    }
		return weakestPayload;
	}
	
	

	/**
	 * 
	 * @param Regex
	 * @return
	 * @throws Exception 
	 */
	private static RegexOperatorTree<StatePayload> convertRegexToTree( RegularExpressionElement<StatePayload>[] regularExpression) throws Exception {
		RegexOperatorTree<StatePayload> regexTree = new RegexOperatorTree<StatePayload>( regularExpression);
		return regexTree;
	}


	@SuppressWarnings("null")
	private static StatePayload getBestPayloadFromTreeNodeCollectionForCharacter( TreeNodeCollection collection, Character theCharacter) {
		StatePayload result = null;
		for ( TreeNode node : collection) {
			if ( node instanceof TerminalNode) {
				@SuppressWarnings("unchecked")
				RegularExpressionElement<StatePayload> nodeValue = (RegularExpressionElement<StatePayload>)((TerminalNode)node).getValue();
				if ( nodeValue.getValue().equals( theCharacter)) { 
					StatePayload currentPayload = nodeValue.getPayload();
					if ( Test.isAssigned(  currentPayload)) {
						if (Test.isUnassigned(  result))
							result = currentPayload;
						else {
							if ( result.getPriority() < currentPayload.getPriority())
								result = currentPayload;
						}
					} 
				}
			}
		}
		return result;
	}
	
	
	
	/**
	 * Speichert den Payload für einen Zielzustand ausgehend von einem Quellzustand durch lesen des angegebenen Zeichens in der gegebenen stateToStateMap.
	 * @param stateToStateMap
	 * @param fromState
	 * @param toState
	 * @param theCharacter
	 * @param thePayloadToSet
	 * @return
	 */
	private static boolean storePayloadPriorityDependentForTransitionFromStateToStateByCharacter( Map<State,Map<State, Map<Character,StatePayload>>> stateToStateMap, State fromState, State toState, Character theCharacter, StatePayload thePayloadToSet) {
	
	  Map<State, Map<Character,StatePayload>> stateToCharacterPayloadMap = stateToStateMap.get( toState);
	  if ( Test.isUnassigned( stateToCharacterPayloadMap)) {
	  	stateToCharacterPayloadMap = new HashMap<State, Map<Character,StatePayload>>();
	  	stateToStateMap.put( toState, stateToCharacterPayloadMap);
	  }	
	  	
	  Map<Character,StatePayload> characterToPayloadMap = stateToCharacterPayloadMap.get( fromState);
	  if ( Test.isUnassigned( characterToPayloadMap)) { 
	  	characterToPayloadMap = new HashMap<Character,StatePayload>();
	  	stateToCharacterPayloadMap.put( fromState, characterToPayloadMap);
	  }

	  StatePayload storedPayload = characterToPayloadMap.get( theCharacter);
	  if ( Test.isAssigned( storedPayload)) {
	  	if ( storedPayload.getPriority() < thePayloadToSet.getPriority()) {
	  		characterToPayloadMap.put( theCharacter, thePayloadToSet);
	  		return true;
	  	}
	  } else {
	  	characterToPayloadMap.put( theCharacter, thePayloadToSet);
	  	return true;
	  }
  	return false;
	}

	/**
	 * Liefert den in der stateToStateMap gespeicherten Payload für das angegebene Zeichen. 
	 * @param stateToStateMap
	 * @param fromState
	 * @param toState
	 * @param theCharacter
	 * @return Den gefundenen Payload oder null.
	 */
	private static StatePayload getPayloadForTransitionFromStateToStateByCharacter( Map<State,Map<State, Map<Character,StatePayload>>> stateToStateMap, State fromState, State toState, Character theCharacter) {

	  Map<State, Map<Character,StatePayload>> stateToCharacterPayloadMap = stateToStateMap.get( toState);
	  if ( Test.isUnassigned( stateToCharacterPayloadMap))
	  	return null;
	  	
	  Map<Character,StatePayload> characterToPayloadMap = stateToCharacterPayloadMap.get( fromState);
	  if ( Test.isUnassigned( characterToPayloadMap)) 
	  	return null;

	  StatePayload storedPayload = characterToPayloadMap.get( theCharacter);
	  if ( Test.isUnassigned( storedPayload))
	  	return null;
	  
	  return storedPayload;
	}
	

	/**
	 * Liefert einen der Payloads mit der höchsten Priorität für einen gegeben Zustand mit Blick auf die stateToStateMap Struktur.
	 * @param stateToStateMap
	 * @param theState
	 * @return
	 */
	@SuppressWarnings("null")
	private static StatePayload getBestPayloadForState( Map<State,Map<State, Map<Character,StatePayload>>> stateToStateMap, State theState) {

	  Map<State, Map<Character,StatePayload>> stateToCharacterPayloadMap = stateToStateMap.get( theState);
	  if ( Test.isUnassigned( stateToCharacterPayloadMap))
	  	return null;

	  StatePayload result = null;
		
	  for ( State<Character, StatePayload> sourceState : stateToCharacterPayloadMap.keySet()) {
	  	Map<Character,StatePayload> characterToPayloadMap = stateToCharacterPayloadMap.get( sourceState);
	  	for ( Character character : characterToPayloadMap.keySet()) {
	  		StatePayload storedPayload = characterToPayloadMap.get( character);
	  		if ( Test.isUnassigned( storedPayload))
	  				continue;
	  		if ( Test.isUnassigned( result))
		  	  result = storedPayload;
	  		else if ( result.getPriority() < storedPayload.getPriority()) {
	  			result = storedPayload;
	  		}	
			}	
		}
	  return result;
	}
	
	
	/**
	 * Liefert einen der Payloads mit der niedrigsten Priorität für einen gegeben Zustand mit Blick auf die stateToStateMap Struktur.
	 * @param stateToStateMap
	 * @param theState
	 * @return
	 */
	@SuppressWarnings("null")
	private static StatePayload getWeakestPayloadForState( Map<State,Map<State, Map<Character,StatePayload>>> stateToStateMap, State theState) {

	  Map<State, Map<Character,StatePayload>> stateToCharacterPayloadMap = stateToStateMap.get( theState);
	  if ( Test.isUnassigned( stateToCharacterPayloadMap))
	  	return null;

	  StatePayload result = null;
		
	  for ( State<Character, StatePayload> sourceState : stateToCharacterPayloadMap.keySet()) {
	  	Map<Character,StatePayload> characterToPayloadMap = stateToCharacterPayloadMap.get( sourceState);
	  	for ( Character character : characterToPayloadMap.keySet()) {
	  		StatePayload storedPayload = characterToPayloadMap.get( character);
	  		if ( Test.isUnassigned( storedPayload))
	  				continue;
	  		if ( Test.isUnassigned( result))
		  	  result = storedPayload;
	  		else if ( result.getPriority() > storedPayload.getPriority()) {
	  			result = storedPayload;
	  		}	
			}	
	  }
	  return result;
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
	@SuppressWarnings("unchecked")
	private static FiniteStateMachine<Character, StatePayload> convertRegexTreeToDfa( RegexOperatorTree<StatePayload> regexTree, StatePayload commonPayload) throws DirectConverterException {
		try {
			
			HashMap<TreeNodeCollection, State<Character, StatePayload>> unhandledStates = new HashMap<TreeNodeCollection, State<Character, StatePayload>>();

			HashMap<TreeNodeCollection, State<Character, StatePayload>> handledStates = new HashMap<TreeNodeCollection, State<Character, StatePayload>>();

			FiniteStateMachine<Character, StatePayload> dfa = new FiniteStateMachine<Character, StatePayload>();

			// add start state as unhandled
			unhandledStates.put( regexTree.getFirstPositions().get( regexTree.getRoot()), dfa.getInitialState());

			// maps the target states to a map of source states with corresponding payloads
			Map<State,Map<State, Map<Character,StatePayload>>> payloadToStateMap = new HashMap<State,Map<State, Map<Character,StatePayload>>>();
			
			StatePayload currentStatePayload = null;

			Set<RegularExpressionElement<StatePayload>> alphabetSubset = new HashSet<RegularExpressionElement<StatePayload>>();
			
			for ( Leaf leaf : regexTree.getLeafSet()) {
				alphabetSubset.add( (RegularExpressionElement<StatePayload> ) leaf.getValue());
			} 
			
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
				
				for ( RegularExpressionElement<StatePayload> currentRegexElement : alphabetSubset) {
					TreeNodeCollection followPositionsOfTerminal = new TreeNodeSet();
					for ( TreeNode node : currentCollection) {
						if ( node instanceof TerminalNode) {
							RegularExpressionElement terminalNodeRegexElement = (RegularExpressionElement)((TerminalNode)node).getValue();
							if ( terminalNodeRegexElement.equals( currentRegexElement)) { // use equals() instead of equalsTotally()
								followPositionsOfTerminal.addAll( regexTree.getFollowPositions().get( node));
							}
						}
					}

					
					// if set not empty, then add set to states
					State<Character, StatePayload> targetState = null;
				
					if ( !followPositionsOfTerminal.isEmpty()) {
						
						// ermittle Übergang-spezifischen Payload 
						currentStatePayload = getBestPayloadFromTreeNodeCollectionForCharacter( currentCollection, currentRegexElement.getValue()); 
						// Oder falls keiner definiert, dann den allgemeinen Payload, sofern es sich um das Ende handelt
						if ( Test.isUnassigned( currentStatePayload)
								&& followPositionsOfTerminal.contains( regexTree.getTerminatorNode()))						
							currentStatePayload = commonPayload;
					
						// Ansonsten wie im Algorithmus von Glushkov / McNaughton and Yamada beschrieben verfahren 
						if ( !handledStates.containsKey( followPositionsOfTerminal) && !unhandledStates.containsKey( followPositionsOfTerminal)) {
							targetState = new State<Character, StatePayload>();
							unhandledStates.put( followPositionsOfTerminal, targetState);
						} else if ( handledStates.containsKey( followPositionsOfTerminal)) {
							targetState = handledStates.get( followPositionsOfTerminal);
						} else {
							targetState = unhandledStates.get( followPositionsOfTerminal);
						}

						// setze Übergang
						dfa.addTransition( targetState, currentRegexElement.getValue());

						// falls das Terminalzeichen # folgt, 
						if ( followPositionsOfTerminal.contains( regexTree.getTerminatorNode())) {
							// dann ist der Zustand ein Endzustand ...
							
							
						  // falls kein spezifischer Payload gegeben ist ..
							if ( Test.isUnassigned( currentStatePayload)) {
								// dann setzte denn allgemeinen Payload
								currentStatePayload = commonPayload;
							}

							// Ist der Folgezustand bereits als Endzustand markiert, ...							
							if ( targetState.isFiniteState()) {
								
								// ... dann, sofern ein Payload gegeben ist
								if ( Test.isAssigned( currentStatePayload)) {
									
									// Teste, ob dem Folgezustand bereits ein Payload zuvor zugewiesen wurde
									if ( Test.isAssigned( targetState.getPayload())) {
										// In diesem Fall vergleiche die Prioritäten den neuen Payloads mit dem alten Payload
										if ( targetState.getPayload().getPriority() < currentStatePayload.getPriority())
										  // und setze den neuen Payload nur, wenn dieser eine höhere Priorität hat
											targetState.setPayload( currentStatePayload); 		
											// merke den Payload und dessen Priorität
 		  								storePayloadPriorityDependentForTransitionFromStateToStateByCharacter( payloadToStateMap, currentState, targetState, currentRegexElement.getValue(), currentStatePayload);										
									} else {
										// anderenfalls (es wurde kein früherer Payload gefunden) , dann detzte den neuen Payload bedingungslos.
										targetState.setPayload( currentStatePayload);
										
										// merke den Payload und dessen Priorität
										storePayloadPriorityDependentForTransitionFromStateToStateByCharacter( payloadToStateMap, currentState, targetState, currentRegexElement.getValue(), currentStatePayload);										
									}
								}
							} else {
								// der Folgezustand ist bislang kein Endzustand
							  // dann setze den Zustand finite
								targetState.setFinite( true);
								// and set payload (be care: currentStatePayload can here be null, if Commonpayload is null)
								if ( Test.isAssigned( currentStatePayload)) {
								  targetState.setPayload( currentStatePayload);
								
								// speichere Datum für die Nachbereitung
								
								  storePayloadPriorityDependentForTransitionFromStateToStateByCharacter(payloadToStateMap, currentState, targetState, currentRegexElement.getValue(), currentStatePayload);
								}
							}
								  
						}		
					}

				}

			}


		  //------------------------------------
			// +++ slightly Modification of algorithm of Glushkov / McNaughton and Yamada +++
			// ---------------------------------------
			// posthumously untie the finate and terminating node by payloads
			Map<UUID, State<Character, StatePayload>> dfaStates = (Map<UUID, State<Character, StatePayload>>) dfa.getStates().clone();
			State<Character, StatePayload> currentDfaState;
			Set<UUID> knownFiniteTerminateStates = new HashSet<UUID>();
			Map<UUID,StatePayload> knownFiniteIntermediateStates = new HashMap<UUID, StatePayload>();
			for ( UUID stateId : dfaStates.keySet()) {
				
				// Untersuche alle Zustände auf Übergänge in den final Zustand
				currentDfaState = dfaStates.get( stateId);
				
				Set< Transition<Character, StatePayload>> transitionSet = currentDfaState.getTransitions();
				Set< Transition<Character, StatePayload>> transitionSetCopy = (Set<Transition<Character, StatePayload>>) currentDfaState.getTransitions().clone();
				for ( Transition<Character, StatePayload> transition : transitionSetCopy) {
					// wenn der Übergang in einen final Zustand führt
					if ( transition.getState().isFiniteState()) {
					  // get original payload
						State<Character, StatePayload> targetState = transition.getState();
						State<Character, StatePayload> sourceState = currentDfaState;

						StatePayload payload = getPayloadForTransitionFromStateToStateByCharacter( payloadToStateMap, sourceState, targetState, transition.getCondition());
					
						if ( Test.isAssigned( payload)) { 

							if ( transition.getState().getElementsOfOutgoingTransitions().isEmpty()) {
								// Fall 1: Es gehen aus diesem Endzustand keine weiteren Übergänge mehr aus.  ->(F)
								if ( !knownFiniteTerminateStates.contains( targetState.getUUID())) {
									// the first transition must not handled
									knownFiniteTerminateStates.add( targetState.getUUID());
									// but update the payload
									targetState.setPayload( payload);
								} else {
									Character terminal = transition.getCondition();
									// Biege den Übergang auf einen neuen Endzustand um.
									transitionSet.remove( transition);
									State<Character, StatePayload> newFinalState = new State<Character, StatePayload>( payload, true);
									dfa.setCurrentState( currentDfaState);
									dfa.addTransition( newFinalState, terminal);
								}

							} else {
								// Fall 2: Es ist ein akzeptierender Zustand, aber es führen auch wieder Übergänge heraus.  ->(F)->
								StatePayload weakestPayload = null;
								// Merke den Zustand mit ausgerechnetem niederwertigsten Payload beim ersten Besuch 
                if ( !knownFiniteIntermediateStates.containsKey( targetState.getUUID())) {
                	weakestPayload = getWeakestPayloadForState( payloadToStateMap, targetState);
                	targetState.setPayload( weakestPayload);
                	knownFiniteIntermediateStates.put( targetState.getUUID(), weakestPayload);
                } else {
                	weakestPayload = knownFiniteIntermediateStates.get( targetState.getUUID());
                }
                
                // Wenn der Payload bereits der niederwertigste ist, dann belasse die Übergänge wie gehabt 
                if ( Test.isUnassigned( weakestPayload) 
                		 || payload.equals( weakestPayload)) {
                	// do nothing
                	continue;
                }
                
              	
                // anderenfalls, wenn der Payload höherwertig ist, dann füge einen akzeptierenden Zwischenzustand ein
                
                // assert payload != weakestpayload
                Character terminal = transition.getCondition();
        
                // füge einen akzeptierenden Zwischenzustand mit dem entsprechenden Payload ein
                State<Character, StatePayload> interState = new State<Character, StatePayload>( payload, true);
                dfa.addTransition( sourceState, interState, terminal);
                // kopiere alle ausgehenden Zustände der Ursprungszustand 
                for ( Transition<Character, StatePayload> targetTransition : targetState.getTransitions()) {
                	dfa.addTransition( interState, targetTransition.getState(), targetTransition.getCondition());
                }

                // entferne die direkte Verbindung in den ursprünglichen targetState
                transitionSet.remove( transition);

                
																
							}
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


	
}
