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

package de.fuberlin.bii.tokenmatcher;

import java.io.Serializable;
import java.util.Collection;

/**
 * Stellt das Interface eines endlichen Automaten (finite state machine, kurz
 * FSM) dar.
 * 
 * @author Johannes Dahlke
 * @author (Daniel Rotar)
 * 
 * @param <ConditionType>
 *            Der Typ der Bedingung für einen Zustandsübergang beim verwendeten
 *            endlichen Automaten.
 * @param <PayloadType>
 *            Der Typ des Inhalts der Zustände beim verwendeten endlichen
 *            Automaten.
 */
public interface DeterministicFiniteAutomata<ConditionType extends Serializable, PayloadType extends Serializable> extends Serializable{

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
	State<ConditionType, PayloadType> changeStateByElement(ConditionType element);

	/**
	 * Prüft, ob ein Übergang für das Lesen des spezifizierten Elementes
	 * definiert ist.
	 * 
	 * @param element
	 *            Das Element, für das geprüft werden soll, ob ein Übergang aus
	 *            dem aktuellen Zustand durch Lesen des Elementes definiert ist.
	 * @return true, wenn es einen Übergang gibt, anderenfalls false.
	 */
	boolean canChangeStateByElement(ConditionType element);

	/**
	 * Gibt den aktuellen Zustand zurück.
	 * 
	 * @return Der aktuelle Zustand zurück.
	 */
	State<ConditionType, PayloadType> getCurrentState();

	/**
	 * Setzt den DFA wieder in den Startzustand zurück.
	 */
	void resetToInitialState();

	/**
	 * Liefert eine Liste mit allen Elementen, die den vom Zustand state
	 * ausgehenden Übergängen zugeordnet sind.
	 * 
	 * @param state
	 *            Der Zustand, von dem die Übergange ausgehen.
	 * @return Alle ausgehenden Elemente zu dem angegebenen Zustand.
	 */
	Collection<ConditionType> getElementsOfOutgoingTransitionsFromState(
			State<ConditionType, PayloadType> state);

}
