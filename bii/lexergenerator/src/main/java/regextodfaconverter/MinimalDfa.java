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

import regextodfaconverter.fsm.FiniteStateMachine;
import tokenmatcher.DeterministicFiniteAutomata;
import tokenmatcher.State;

/**
 * Adapter zur Anpassung an das DFA Interface des TokenMatchers. Garantiert,
 * dass der gekapselte FSA auch sicher ein DFA ist.
 * 
 * @author workstation
 * 
 * @param <ConditionType>
 *            Der Typ der Bedingung für einen Zustandsübergang beim verwendeten
 *            endlichen Automaten.
 * @param <PayloadType>
 *            Der Typ des Inhalts der Zustände beim verwendeten endlichen
 *            Automaten.
 */
public class MinimalDfa<ConditionType extends Comparable<ConditionType>, PayloadType>
		implements DeterministicFiniteAutomata<ConditionType, PayloadType> {

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
			if (!finiteStateMachine.isDeterministic()) {
				NfaToDfaConverter<ConditionType, PayloadType> converter = new NfaToDfaConverter<ConditionType, PayloadType>();
				finiteStateMachine = converter.convertToDfa(finiteStateMachine);
			}
			DfaMinimizer<ConditionType, PayloadType> minimizer = new DfaMinimizer<ConditionType, PayloadType>();
			finiteStateMachine = minimizer
					.convertToMimimumDfa(finiteStateMachine);
		} catch (Exception e) {
			throw new ConvertExecption(
					"Cannot convert given fsa to minimal dfa.");
		}
	}

	public State<PayloadType> changeStateByElement(ConditionType element) {
		return finiteStateMachine.changeState(element);
	}

	public boolean canChangeStateByElement(ConditionType element) {
		return finiteStateMachine.canChangeState(element);
	}

	public State<PayloadType> getCurrentState() {
		return finiteStateMachine.getCurrentState();
	}

	public void resetToInitialState() {
		finiteStateMachine.resetToInitialState();
	}

}
