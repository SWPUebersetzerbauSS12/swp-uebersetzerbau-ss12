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

package de.fuberlin.bii.regextodfaconverter;

import de.fuberlin.bii.regextodfaconverter.fsm.FiniteStateMachine;
import de.fuberlin.bii.regextodfaconverter.fsm.State;
import de.fuberlin.bii.regextodfaconverter.fsm.excpetions.NullStateException;
import de.fuberlin.bii.regextodfaconverter.fsm.excpetions.StateNotReachableException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Stellt einen Konverter dar, der aus aus einem deterministischen endlichen
 * Automaten (deterministic finite automaton, kurz DFA) einen neuen minimalen
 * deterministischen endlichen Automaten erstellt.
 * 
 * @author Alexander Niemeier, Philipp Schröter
 * 
 * @param <TransitionConditionType>
 *            Der Typ der Bedingung für einen Zustandsübergang beim verwendeten
 *            endlichen Automaten.
 * @param <StatePayloadType>
 *            Der Typ des Inhalts der Zustände beim verwendeten endlichen
 *            Automaten.
 */
public class DfaMinimizer<TransitionConditionType extends Serializable, StatePayloadType extends Serializable> {
	private int startPos = 0;
	
	private FiniteStateMachine<TransitionConditionType, StatePayloadType> input;			//Eingabe
	private HashMap<UUID, State<TransitionConditionType, StatePayloadType>> originalStates;	//enthält die ursprünglichen Zustände der Eingabe, die bearbeitet werden
	private boolean initialeStateIsMarked;													//gibt an, ob der Anfangszustand markiert ist
	private UUID[] states;																	//enthält alle Zustandsbezeichnungen
	private LinkedList<String> transitions;													//enthält alle Übergänge, die vorhanden bzw. möglich sind
	private HashMap<String, String> transitionsMap;											//enthält alle Zustände und Übergänge (Schema: Zustand$Übergang) und als Wert den Zielzustand
	private LinkedList<String> markedPairs;													//enthält alle markierten Paare, die nicht verschmolzen werden können
	private LinkedList<String> unmarkedPairs;												//enthält alle unmarkierten Paare, die eventuell verschmolzen werden können
	
	/**
	 * Macht aus dem angegebenen (deterministischen) endlichen Automaten einen minimalen deterministischen endlichen Automaten.
	 * @param finiteStateMachine Der (deterministischen) endlichen Automaten der minimiert werden soll.
	 * @return Der minimierte deterministischen endlichen Automaten.
	 */
	public FiniteStateMachine<TransitionConditionType, StatePayloadType> convertToMimimumDfa(
			FiniteStateMachine<TransitionConditionType, StatePayloadType> finiteStateMachine)
			throws NotDeterministicException {
		this.input = finiteStateMachine;
		this.transitions = new LinkedList<String>();
		
		//hole die Zustände und überführe sie in ein Array vom Typ UUID
		this.originalStates = finiteStateMachine.getStates();
		Collection<State<TransitionConditionType, StatePayloadType>> collect = this.originalStates.values();
		this.states = new UUID[collect.size()];
		Iterator<State<TransitionConditionType, StatePayloadType>> iterCollection = collect.iterator();
		int counter = 0;
		while(iterCollection.hasNext()){
			this.states[counter] = iterCollection.next().getUUID();
			counter++;
		}
		
		//prüfe, ob es kein deterministischer Automat ist
		if (!finiteStateMachine.isDeterministic())
		{
			throw new NotDeterministicException("the delivered finite state machine is not deterministic");
		}
		
		//trenne die Zustände und die Übergänge
		String[] finiteStatesString = finiteStateMachine.toString().split("\n");
		//erstelle eine Map der Zustände und möglicher Übergänge mitsamt der neuen Zustände
		startPos = finiteStatesString.length;
		filterTransitions(finiteStatesString); //wird nicht mehr gebraucht
		
		//erstelle eine Liste mit allen disjunkten Zustandspaaren
		LinkedList<String> statePairList = new LinkedList<String>();
		LinkedList<UUID> doneList = new LinkedList<UUID>();
		for(int i = 0; i < this.states.length; i++){
			for(int j = 0; j < this.states.length; j++){
				boolean alreadyDoneOrEqual = false;
				if(!this.states[i].equals(this.states[j])){
					Iterator<UUID> iter = doneList.iterator();
					while(iter.hasNext()){
						if(this.states[j].equals(iter.next())){
							alreadyDoneOrEqual = true;
							break;
						}
					}
				}
				else{
					alreadyDoneOrEqual = true;
				}
				if(!alreadyDoneOrEqual){
					statePairList.add(this.states[i].toString()+"$"+this.states[j].toString());
					doneList.add(this.states[i]);
				}
			}
		}
		
		//die Tabelle, wo die Markierungen gesetzt werden, wo genau eine ein Endzustand ist, erstellen
		this.markedPairs = new LinkedList<String>();
		this.unmarkedPairs = new LinkedList<String>();
		//bilde die Zustandspaare
		Iterator<String> iIter = statePairList.iterator();
		while(iIter.hasNext()){
			String[] currentPair = iIter.next().split("\\$");
			//und wenn genau einer von beiden ein Endzustand ist...
			State<TransitionConditionType, StatePayloadType> current1 = finiteStateMachine.getStateByUUID(UUID.fromString(currentPair[0]));
			State<TransitionConditionType, StatePayloadType> current2 = finiteStateMachine.getStateByUUID(UUID.fromString(currentPair[1]));
			if(		(current1.isFiniteState() && (!current2.isFiniteState())) ||
					(!current1.isFiniteState()) && (current2.isFiniteState())	){
				//wenn einer der beiden ein Endzustand ist
				//markiere ihn
				this.markedPairs.addFirst(currentPair[0]+"$"+currentPair[1]);
			}
			else{
				//ansonsten vermerke, ihn weil er noch benötigt wird
				this.unmarkedPairs.addFirst(currentPair[0]+"$"+currentPair[1]);
			}
		}

		//prüfe, welche Eingaben sich gleichen, um diese Zustände zu verschmelzen
		while(markStates()){}
		
		//erstelle alle Felder neu mit dem Inhalt aus den alten
		if(this.unmarkedPairs.isEmpty()){								//wenn es keine Einträge gibt, ist der Automat bereits minimal 
			return finiteStateMachine;						//dann wird einfach die Maschine neu erzeugt und zurückgegeben
		}
				
		//prüfe, ob der Startzustand markiert ist
		Iterator<String> iterStart = this.markedPairs.iterator();
		this.initialeStateIsMarked = false;
		while(iterStart.hasNext()){
			String[] currentMarkedPair = iterStart.next().split("\\$");
			State<TransitionConditionType, StatePayloadType> currentMarkedState1 = this.input.getStateByUUID(UUID.fromString(currentMarkedPair[0]));
			State<TransitionConditionType, StatePayloadType> currentMarkedState2 = this.input.getStateByUUID(UUID.fromString(currentMarkedPair[1]));
			if(currentMarkedState1.isInitialState()){
				this.initialeStateIsMarked = true;
			}
			if(currentMarkedState2.isInitialState()){
				this.initialeStateIsMarked = true;
			}
		}

		//sorge dafür, dass der Anfangszustand in der Reihenfolge der markierten Zustände vorne ist
		//im nächsten Schritt, werden die Zustände in der Matrix so verändert, dass ein Teil der Zustände nicht mehr angegangen wird und der Anfangszustand soll nicht verschwinden
		boolean initialNotFound = false;
		int runs = 0;
		if(this.initialeStateIsMarked){
			while(!initialNotFound && runs < this.markedPairs.size()){
				String nextMarkedState = this.markedPairs.peekFirst();
				String[] currentMarkedPair = nextMarkedState.split("\\$");
				if(currentMarkedPair[1].matches(this.input.getInitialState().getUUID().toString())){
					this.markedPairs.remove(currentMarkedPair[0]+"$"+currentMarkedPair[1]);
					this.markedPairs.add(currentMarkedPair[1]+"$"+currentMarkedPair[0]);
					initialNotFound = true;
				}
				runs++;
			}
		}
		else{
			while(!initialNotFound && runs < this.unmarkedPairs.size()){
				String nextMarkedState = this.unmarkedPairs.peekFirst();
				String[] currentMarkedPair = nextMarkedState.split("\\$");
				if(currentMarkedPair[1].matches(this.input.getInitialState().getUUID().toString())){
					this.unmarkedPairs.remove(currentMarkedPair[0]+"$"+currentMarkedPair[1]);
					this.unmarkedPairs.add(currentMarkedPair[1]+"$"+currentMarkedPair[0]);
					initialNotFound = true;
				}
				runs++;
			}
		}
		
		if(!this.unmarkedPairs.isEmpty()){
			//gehe durch die Zustände und prüfe, welchem Zustand gegangen wird, der nachher nicht mehr existert
			for(int i = 0; i < states.length; i++){
				Iterator<String> iterTransitions = this.transitions.iterator();
				while(iterTransitions.hasNext()){
					Iterator<String> iterUnmarkedStatePairs = this.unmarkedPairs.iterator();
					String currentTransition  = iterTransitions.next();
					while(iterUnmarkedStatePairs.hasNext()){
						String[] currentUnmarkedPair = iterUnmarkedStatePairs.next().split("\\$");
						//wenn der Zustand in einen Zustand führt, den es später nicht mehr geben wird
						String currentDestination = this.transitionsMap.get(states[i].toString()+"$"+currentTransition);
						if(currentDestination != null){
							if(currentDestination.matches(currentUnmarkedPair[1])){
								//ersetze ihn durch den, der es später wird
								this.transitionsMap.put(states[i].toString()+"$"+currentTransition, currentUnmarkedPair[0]);
							}
						}
					}
				}
			}
		}
		
		//erstelle den neuen Automaten
		return finiteStateMachine;
	}

	/**
	 * Erstellt die HashMap für transitionsMap, die Schlüssel haben das Schema: Zustand$Übergang und die Werte enthalten den Zustand, zu dem man gelangt
	 * @param states Die Statuse mitsamt Übergängen
	 *
	 */
	private void filterTransitions(String[] statesString){
		this.transitionsMap = new HashMap<String, String>();
		//parse das String Array
		for(int i = startPos; i < statesString.length; i++){
			//wenn der String nicht mit einem Tab beginnt, also ein Zustand ist
			if(!statesString[i].matches("^\\t")){
				for(int j = i+1; j < statesString.length; j++){
					if(statesString[j].matches("^\\t.+")){
						if(!statesString[j].matches(".+No outgoing transitions.+")){
							String noTab = statesString[j].replace("\t", "");													//entferne den Tab
							String[] transitionAndNewState = noTab.split(" -> ");												//entferne den Pfeil
							addTransition(transitionAndNewState[0]);															//füge den Übergang in die Liste hinzu, falls sie nicht schon vorhanden ist
							this.transitionsMap.put(statesString[i].replace(">", "")+"$"+transitionAndNewState[0], transitionAndNewState[1]);	//speicher die Angaben in der Hashmap
						}
					}
					else{
						//wenn ein neuer Zustand kommt, brich ab und überspringe alle bisher gegangenen Stellen
						i = j-1;
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Fügt Übergänge in die Liste der Übergänge hinzu, i.e. transitions
	 */
	private void addTransition(String transition){
		Iterator<String> iterTrans = this.transitions.iterator();
		boolean isInList = false;
		while(iterTrans.hasNext()){
			if(iterTrans.next().matches(transition)){
				isInList = true;
				break;
			}
		}
		if(!isInList){
			this.transitions.add(transition);
		}
	}
	
	/**
	 * Durchläuft alle Zustandspaare in unmarkedPairs und prüft, welche noch markiert werden können
	 * @return Gibt zurück, ob ein Zustandspaar gefunden wurde, der markiert wurde
	 */
	private boolean markStates(){
		//initialisiere die Rückgabe
		boolean output = false;
		//der Iterator, der durch die Übergänge geht
		Iterator<String> iterTransitions = this.transitions.iterator();
		//gehe durch die Übergänge
		while(iterTransitions.hasNext()){
			String currentTransition = iterTransitions.next();
			//gehe durch die bisher unmarkierten Zustandspaare
			Iterator<String> iterUnmarked = this.unmarkedPairs.iterator();
			while(iterUnmarked.hasNext()){
				//hole das nächste (bisher) unmarkierte Element zum Vergleichen
				String[] currentStates = iterUnmarked.next().split("\\$");
				String goalState1 = this.transitionsMap.get(currentStates[0]+"$"+currentTransition);
				String goalState2 = this.transitionsMap.get(currentStates[1]+"$"+currentTransition);
				//prüfe, ob die Zustände in die gegangen wird, markiert sind
				if(this.markedPairs.contains(goalState1+"$"+goalState2) || this.markedPairs.contains(goalState2+"$"+goalState1)){
					this.unmarkedPairs.remove(currentStates[0]+"$"+currentStates[1]);
					this.markedPairs.add(currentStates[1]+"$"+currentStates[1]);
					//wenn etwas markiert wurde, gib true zurück, um anzuzeigen, dass etwas markiert wurde
					output = true;
					break;
				}
			}
		}
		return output;
	}
	
	/**
	 * 
	 * @param noUnmarkedStates Gibt an, ob Zustandspaare unmarkiert sind
	 * @return Der Automat, der zurückgegeben wird
	 */
	@SuppressWarnings("unused")
	private FiniteStateMachine<TransitionConditionType, StatePayloadType> createFiniteStateMachine(boolean noUnmarkedStates){
		FiniteStateMachine<TransitionConditionType, StatePayloadType> output = new FiniteStateMachine<TransitionConditionType, StatePayloadType>();
		if(noUnmarkedStates){
			//wenn keine Zustände markiert sind, überführe einfach die Zustände der Input-Maschine in eine neue
			Collection<State<TransitionConditionType, StatePayloadType>> collection = this.input.getStates().values();
			LinkedList<State<TransitionConditionType, StatePayloadType>> toAddStates = new LinkedList<State<TransitionConditionType, StatePayloadType>>();
			//sortiere die Zustände in eine LinkedList
			Iterator<State<TransitionConditionType, StatePayloadType>> iterCollection = collection.iterator(); 
			while(iterCollection.hasNext()){
				toAddStates.addFirst(iterCollection.next());
			}
			//gehe die Zustände durch und versuche sie in den Automaten zu packen
			while(!toAddStates.isEmpty()){
				//hole das nächste Element
				String currentState = toAddStates.pollFirst().getUUID().toString();
				//erstelle einen Iterator der ausgehenden Übergänge
				Iterator<TransitionConditionType> iterTrans = this.input.getStateByUUID(UUID.fromString(currentState)).getElementsOfOutgoingTransitions().iterator();
				while(iterTrans.hasNext()){
					//prüfe, ob man von diesem Zustand mit diesem Übergang zu einem anderen Zustand kommt
					TransitionConditionType currentTransition = iterTrans.next();
					String currentDestination = this.transitionsMap.get(currentState+"$"+currentTransition.toString());
					if(currentDestination != null){
						try {
							output.addTransition(this.input.getStateByUUID(UUID.fromString(currentState)), this.input.getStateByUUID(UUID.fromString(currentDestination)), currentTransition);
						} catch (NullStateException e) {
							e.printStackTrace();
						} catch (StateNotReachableException e) {
							//schiebe diesen Zustand vorerst ans Ende der Liste
							toAddStates.addLast(this.input.getStateByUUID(UUID.fromString(currentState)));
							break;
						}
					}
				}
			}
			return output;
		}
		else{
			//ansonsten erstelle eine neue Maschine mit den Veränderungen
			if(this.initialeStateIsMarked){
				//gehe zuerst die Zustände durch, die verschmolzen werden können, weil dort der Anfangszustand ist, und versuche sie in den Automaten zu packen
				LinkedList<String> markedStates = getStatesFromPairList(this.markedPairs);
				//beginne damit den Anfangszustand zu finden
				Iterator<String> iterFindStart = markedStates.iterator();
				String start = this.input.getInitialState().getUUID().toString();
				while(iterFindStart.hasNext()){
					String currentState = iterFindStart.next();
					if(currentState.matches(start)){
						Collection<TransitionConditionType>  startTrans = this.input.getInitialState().getElementsOfOutgoingTransitions();
						Iterator<TransitionConditionType> iterStartTrans = startTrans.iterator();
						while(iterStartTrans.hasNext()){
							TransitionConditionType currentTransition = iterStartTrans.next();
							String currentDestination = this.transitionsMap.get(currentState+"$"+currentTransition.toString());
							if(currentDestination != null){
								try {
									output.addTransition(output.getInitialState(), this.input.getStateByUUID(UUID.fromString(currentDestination)), currentTransition);
								} catch (NullStateException e) {
									e.printStackTrace();
								} catch (StateNotReachableException e) {
								}
							}
						}
					}
				}
				//entferne den Startzustand
				markedStates.remove(start);
				//füge dann die restlichen markierten in den neuen Automaten ein
				while(!markedStates.isEmpty()){
					String currentState = markedStates.pollFirst();
					Collection<TransitionConditionType>  transitionsOfCurrentState = this.input.getStateByUUID(UUID.fromString(currentState)).getElementsOfOutgoingTransitions();
					Iterator<TransitionConditionType> iterTrans = transitionsOfCurrentState.iterator();
					while(iterTrans.hasNext()){
						TransitionConditionType currentTransition = iterTrans.next();
						String currentDestination = this.transitionsMap.get(currentState+"$"+currentTransition.toString());
						//if(currentDestination != null){
							try {
								output.addTransition(this.input.getStateByUUID(UUID.fromString(currentState)), this.input.getStateByUUID(UUID.fromString(currentDestination)), currentTransition);
							} catch (NullStateException e) {
								e.printStackTrace();
							} catch (StateNotReachableException e) {
								markedStates.addLast(currentState);
								break;
							}
						//}
					}
				}
				//und danach die unmarkierten
				//überführe einfach die markierten Zustände der Input-Maschine in eine neue
				//gehe die Zustände durch und versuche sie in den Automaten zu packen
				while(!this.unmarkedPairs.isEmpty()){
					String[] currentPair = this.unmarkedPairs.pollFirst().split("\\$");
					Collection<TransitionConditionType>  transitionsOfCurrentState = this.input.getStateByUUID(UUID.fromString(currentPair[0])).getElementsOfOutgoingTransitions();
					Iterator<TransitionConditionType> iterTrans = transitionsOfCurrentState.iterator();
					while(iterTrans.hasNext()){
						TransitionConditionType currentTransition = iterTrans.next();
						String currentDestination = this.transitionsMap.get(currentPair[0]+"$"+currentTransition.toString());
						//if(currentDestination != null){
							try {
								output.addTransition(this.input.getStateByUUID(UUID.fromString(currentPair[0])), this.input.getStateByUUID(UUID.fromString(currentDestination)), currentTransition);
							} catch (NullStateException e) {
								e.printStackTrace();
							} catch (StateNotReachableException e) {
								this.unmarkedPairs.addLast(currentPair[0]+"$"+currentPair[1]);
								break;
							}
						//}
					}
				}
				return output;
			}
			else{
				//überführe einfach die unmarkierten Zustände der Input-Maschine in eine neue
				//gehe die Zustände durch und versuche sie in den Automaten zu packen
				//beginne damit den Anfangszustand zu finden
				Iterator<String> iterFindStart = this.unmarkedPairs.iterator();
				String start = this.input.getInitialState().getUUID().toString();
				while(iterFindStart.hasNext()){
					String[] currentPair = iterFindStart.next().split("\\$");
					if(currentPair[0].matches(start)){
						Collection<TransitionConditionType>  startTrans = this.input.getInitialState().getElementsOfOutgoingTransitions();
						Iterator<TransitionConditionType> iterStartTrans = startTrans.iterator();
						while(iterStartTrans.hasNext()){
							TransitionConditionType currentTransition = iterStartTrans.next();
							String currentDestination = this.transitionsMap.get(currentPair[0]+"$"+currentTransition.toString());
							//if(currentDestination != null){
								try {
									output.addTransition(output.getInitialState(), this.input.getStateByUUID(UUID.fromString(currentDestination)), currentTransition);
								} catch (NullStateException e) {
									e.printStackTrace();
								} catch (StateNotReachableException e) {
									//tue nichts
								}
								//entferne den Startzustand
								this.unmarkedPairs.remove(this.input.getInitialState().getUUID().toString()+"$"+currentDestination);
							//}
						}
					}
				}
				while(!this.unmarkedPairs.isEmpty()){
					String[] currentPair = this.unmarkedPairs.pollFirst().split("\\$");
					Collection<TransitionConditionType>  transitionsOfCurrentState = this.input.getStateByUUID(UUID.fromString(currentPair[0])).getElementsOfOutgoingTransitions();
					Iterator<TransitionConditionType> iterTrans = transitionsOfCurrentState.iterator();
					while(iterTrans.hasNext()){
						TransitionConditionType currentTransition = iterTrans.next();
						String currentDestination = this.transitionsMap.get(currentPair[0]+"$"+currentTransition.toString());
						//if(currentDestination != null){
							try {
								output.addTransition(this.input.getStateByUUID(UUID.fromString(currentPair[0])), this.input.getStateByUUID(UUID.fromString(currentDestination)), currentTransition);
							} catch (NullStateException e) {
								e.printStackTrace();
							} catch (StateNotReachableException e) {
								this.unmarkedPairs.addLast(currentPair[0]+"$"+currentPair[1]);
								break;
							}
						//}
					}
				}
				//und danach die markierten
				LinkedList<String> markedStates = getStatesFromPairList(this.markedPairs);
				//füge dann die restlichen markierten in den neuen Automaten ein
				while(!markedStates.isEmpty()){
					String currentState = markedStates.pollFirst();
					Collection<TransitionConditionType>  transitionsOfCurrentState = this.input.getStateByUUID(UUID.fromString(currentState)).getElementsOfOutgoingTransitions();
					Iterator<TransitionConditionType> iterTrans = transitionsOfCurrentState.iterator();
					while(iterTrans.hasNext()){
						TransitionConditionType currentTransition = iterTrans.next();
						String currentDestination = this.transitionsMap.get(currentState+"$"+currentTransition.toString());
						if(currentDestination != null){
							try {
								output.addTransition(this.input.getStateByUUID(UUID.fromString(currentState)), this.input.getStateByUUID(UUID.fromString(currentDestination)), currentTransition);
							} catch (NullStateException e) {
								e.printStackTrace();
							} catch (StateNotReachableException e) {
								markedStates.addLast(currentState);
								break;
							}
						}
					}
				}
				return output;
			}
		}
	}
	
	/**
	 *
	 * @param PairList Die Liste mit Paaren von Zuständen, die auseinander gebracht werden sollen
	 * @return Die Liste, die alle disjunkten Vorkommen von Zuständen enthält
	 */
	private LinkedList<String> getStatesFromPairList(LinkedList<String> PairList){
		LinkedList<String> output = new LinkedList<String>();
		Iterator<String> iter = PairList.iterator();
		while(iter.hasNext()){
			String[] currentPair = iter.next().split("\\$");
			if(!output.contains(currentPair[0])){
				output.add(currentPair[0]);
			}
			if(!output.contains(currentPair[1])){
				output.add(currentPair[1]);
			}
		}
		return output;
	}
}
