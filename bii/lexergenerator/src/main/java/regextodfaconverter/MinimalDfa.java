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

package regextodfaconverter;

import java.io.Serializable;
import java.util.Collection;

import regextodfaconverter.fsm.FiniteStateMachine;
import tokenmatcher.DeterministicFiniteAutomata;
import tokenmatcher.State;

/**
 * Adapter zur Anpassung an das DFA Interface des TokenMatchers. Garantiert,
 * dass der gekapselte FSA auch sicher ein DFA ist.
 * 
 * @author Johannes Dahlke
 * @author Daniel Rotar
 * 
 * @param <ConditionType>
 *            Der Typ der Bedingung für einen Zustandsübergang beim verwendeten
 *            endlichen Automaten.
 * @param <PayloadType>
 *            Der Typ des Inhalts der Zustände beim verwendeten endlichen
 *            Automaten.
 */
public class MinimalDfa<ConditionType extends Comparable<ConditionType>, PayloadType>
		implements DeterministicFiniteAutomata<ConditionType, PayloadType>, Serializable {

	/**
	 * UID für die Serialisierung/Abspeicherung als *.dfa 
	 */
	private static final long serialVersionUID = -7404462323431169070L;
	
	/**
	 * Der endliche Automat auf dem gearbeitet wird.
	 */
	private FiniteStateMachine<ConditionType, PayloadType> finiteStateMachine;

	/**
	 * Erstellt ein neues MinimalDfa Objekt und garantiert dabei, dass der
	 * endliche Automat ein DFA ist und minimal ist.
	 * 
	 * @param finiteStateMachine
	 *            Der endliche Automat, aus dem der minimale DFA erstellt werden
	 *            soll.
	 * @throws ConvertExecption
	 *             Wenn bei der konvertierung in ein DFA oder bei der
	 *             minimierung des DFAs ein Fehler auftritt.
	 */
	public MinimalDfa(
			FiniteStateMachine<ConditionType, PayloadType> finiteStateMachine)
			throws ConvertExecption {
		super();
		this.finiteStateMachine = finiteStateMachine;
		try {
			if (!this.finiteStateMachine.isDeterministic()) {
				NfaToDfaConverter<ConditionType, PayloadType> converter = new NfaToDfaConverter<ConditionType, PayloadType>();
				this.finiteStateMachine = converter.convertToDfa(this.finiteStateMachine);
			}
			DfaMinimizer<ConditionType, PayloadType> minimizer = new DfaMinimizer<ConditionType, PayloadType>();
			this.finiteStateMachine = minimizer
					.convertToMimimumDfa(this.finiteStateMachine);
		} catch (Exception e) {
			throw new ConvertExecption(
					"Cannot convert given fsa to minimal dfa.");
		}
	}

	/**
	 * Wechselt in einen anderen Zustand, sofern es einen Übergang in der
	 * Übergangstabelle ausgehend vom aktuellen Zustand in den angegebenen
	 * Element gibt.
	 * 
	 * @param element
	 *            Das Element, welches den Übergang definiert.
	 * @return Den neuen Zustand oder null, falls es keinen Übergang in der
	 *         Übergangstabelle gibt.
	 */
	public State<ConditionType, PayloadType> changeStateByElement(
			ConditionType element) {
		return finiteStateMachine.changeState(element);
	}

	/**
	 * Prüft, ob ein Übergang für das Lesen des spezifizierten Elementes
	 * definiert ist.
	 * 
	 * @param element
	 *            Das Element, für das geprüft werden soll, ob ein Übergang aus
	 *            dem aktuellen Zustand durch Lesen des Elementes definiert ist.
	 * @return true, wenn es einen Übergang gibt, anderenfalls false.
	 */
	public boolean canChangeStateByElement(ConditionType element) {
		return finiteStateMachine.canChangeState(element);
	}

	/**
	 * Gibt den aktuellen Zustand zurück.
	 * 
	 * @return Der aktuelle Zustand.
	 */
	public State<ConditionType, PayloadType> getCurrentState() {
		return finiteStateMachine.getCurrentState();
	}

	/**
	 * Setzt den DFA wieder in den Startzustand zurück.
	 */
	public void resetToInitialState() {
		finiteStateMachine.resetToInitialState();
	}

	/**
	 * Liefert eine Liste mit allen Elementen, die den vom Zustand state
	 * ausgehenden Übergängen zugeordnet sind.
	 * 
	 * @param state
	 *            Der Zustand, von dem die Übergange ausgehen.
	 * @return Alle ausgehenden Elemente zu dem angegebenen Zustand.
	 */
	public Collection<ConditionType> getElementsOfOutgoingTransitionsFromState(
			State<ConditionType, PayloadType> state) {
		return state.getElementsOfOutgoingTransitions();
	}

}
