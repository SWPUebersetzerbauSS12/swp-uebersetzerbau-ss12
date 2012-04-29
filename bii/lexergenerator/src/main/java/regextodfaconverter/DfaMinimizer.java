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

/**
 * Stellt einen Konverter dar, der aus aus einem deterministischen endlichen
 * Automaten (deterministic finite automaton, kurz DFA) einen neuen minimalen
 * deterministischen endlichen Automaten erstellt.
 * 
 * @author ?
 * 
 * @param <TransitionConditionType>
 *            Der Typ der Bedingung für einen Zustandsübergang beim verwendeten
 *            endlichen Automaten.
 * @param <StatePayloadType>
 *            Der Typ des Inhalts der Zustände beim verwendeten endlichen
 *            Automaten.
 */
public class DfaMinimizer<TransitionConditionType extends Comparable<TransitionConditionType>, StatePayloadType> {

	/**
	 * Macht aus dem angegebenen (deterministischen) endlichen Automaten einen minimalen deterministischen endlichen Automaten.
	 * @param finiteStateMachine Der (deterministischen) endlichen Automaten der minimiert werden soll.
	 * @return Der minimierte deterministischen endlichen Automaten.
	 */
	public FiniteStateMachine<TransitionConditionType, StatePayloadType> convertToMimimumDfa(
			FiniteStateMachine<TransitionConditionType, StatePayloadType> finiteStateMachine) {
		if (!finiteStateMachine.isDeterministic())
		{
			//TODO: Exception werfen, da nur ausgehend von einem DFA ein minimaler DFA erstellt werden kann.
		}
		// TODO convertToMimimumDfa implementieren.
		return finiteStateMachine;
	}

}
