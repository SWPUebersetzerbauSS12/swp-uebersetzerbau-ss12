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
 * Authors: Daniel Rotar
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package regextodfaconverter.fsm;

import java.util.HashSet;
import java.util.UUID;

import regextodfaconverter.fsm.excpetions.TransitionAlreadyExistsException;

/**
 * Stellt einen Zustand eines endlicher Automaten (bzw. einer Zustandsmaschine) dar.
 * @author Daniel Rotar
 *
 * @param <TransitionConditionType> Der Typ der Bedingung für einen Zustandübergang.
 * @param <PayloadType> Der Typ des Inhalts.
 */
public class State
	<
		TransitionConditionType extends Comparable<TransitionConditionType>,
		PayloadType
	> 
	implements 
		Comparable<State<TransitionConditionType, PayloadType>>, 
		tokenmatcher.State<PayloadType>
{
	
	/**
	 * Die eindetige UUID dieses Zustandes.
	 */
	private UUID _uuid;
	/**
	 * Der in diesem Zustand hinterlegte Inhalt.
	 */
	private PayloadType _payload;
	/**
	 * Die Übergünge, die von diesem Zustand möglich sind.
	 */
	private HashSet<Transition<TransitionConditionType, PayloadType>> _transitions;
	/**
	 * Der Zustandstyp.
	 */
	private StateType _type;	
	
	
	
	/**
	 * Gibt die eindetige UUID dieses Zustandes zurück.
	 * @return Die eindetige UUID dieses Zustandes.
	 */
	public UUID getUUID()
	{
		return _uuid;
	}
	/**
	 * Gibt den in diesem Zustand hinterlegte Inhalt zurück.
	 * @return Der in diesem Zustand hinterlegte Inhalt.
	 */
	public PayloadType getPayload()
	{
		return _payload;
	}
	/**
	 * Setzt den in diesem Zustand zu hinterlegenden Inhalt fest.
	 * @param payload Der in diesem Zustand zu hinterlegende Inhalt.
	 */
	public void setPayload(PayloadType payload)
	{
		_payload = payload;
	}
	/**
	 * Gibt die Übergänge, die von diesem Zustand möglich sind zurück.
	 * @return Die Übergänge, die von diesem Zustand möglich sind.
	 */
	public HashSet<Transition<TransitionConditionType, PayloadType>> getTransitions()
	{
		return _transitions;
	}
	/**
	 * Gibt den Zustandstyp zurück.
	 * @return Der Zustandstyp.
	 */
	public StateType getType()
	{
		//Ein Zustand ohne ausgehende Übergänge ist immer ein Endzustand, es sei den er ist ein Startzustand.
		if (_type != StateType.INITIAL && _transitions.isEmpty())
		{
			return StateType.FINITE;
		}
		return _type;
	}
	/**
	 * Legt den Zustandstyp fest.
	 * @param type Der Zustandstyp.
	 */
	void setType(StateType type)
	{
		_type = type;
	}	
	/**
	 * Legt den Zustandstyp auf FINITE fest.
	 */
	public void SetTypeToFinite()
	{
		_type = StateType.FINITE;
	}
	/**
	 * Legt den Zustandstyp auf DEFAULT fest.
	 */
	public void SetTypeToDefault()
	{
		_type = StateType.DEFAULT;
	}
	/**
	 * Gibt an, ob der Zustandtyp dieses Zustands FINITE ist.
	 */
	public boolean isFiniteState() {
		if (_type == StateType.FINITE)
		{
			return true;
		}
		else
		{
			return false;
		}		
	}
	/**
	 * Gibt an, ob der Zustandtyp dieses Zustands INITIAL ist.
	 */
	public boolean isInitialState() {
		if (_type == StateType.INITIAL)
		{
			return true;
		}
		else
		{
			return false;
		}		
	}
	/**
	 * Gibt an, ob der Zustandtyp dieses Zustands Default ist.
	 */
	public boolean isDefaultState() {
		if (_type == StateType.DEFAULT)
		{
			return true;
		}
		else
		{
			return false;
		}		
	}	


	
	/**
	 * Fügt dem aktuellen Zustand einen Nachfolgezustand hinzu.
	 * @param condition Die Bedingung für den Zustandübergang.
	 * @param state Der einzufügende Nachfolgezustand.
	 * @throws TransitionAlreadyExistsException Wenn der Übergang bereits vorhanden ist.
	 */
	void addState(TransitionConditionType condition, State<TransitionConditionType, PayloadType> state) throws TransitionAlreadyExistsException
	{
		if (!_transitions.add(new Transition<TransitionConditionType, PayloadType>(condition,state)))
		{
			throw new TransitionAlreadyExistsException();
		}
	}
	
	
	public int compareTo(State<TransitionConditionType, PayloadType> o) {
		if (o.getUUID() == getUUID())
		{
			return 0;
		}
		else
		{
			return -1;
		}		
	}
	
	
	
	/**
	 * Erstellt ein neues State Objekt.
	 */
	public State()
	{
		_uuid = UUID.randomUUID();
		_payload = null;
		_transitions = new HashSet<Transition<TransitionConditionType, PayloadType>>();
		_type = StateType.DEFAULT;
	}
	/**
	 * Erstellt ein neues State Objekt.
	 * @param payload Der in diesem Zustand hinterlegte Inhalt.
	 */
	public State(PayloadType payload)
	{
		this();
		_payload = payload;		
	}
	/**
	 * Erstellt ein neues State Objekt.
	 * @param isFinal Gibt an, ob es sich bei diesem Zustand um einen Endzustand handelt.
	 */
	public State(boolean isFinal)
	{
		this();
		if (isFinal) _type = StateType.FINITE;
	}
	/**
	 * Erstellt ein neues State Objekt.
	 * @param payload Der in diesem Zustand hinterlegte Inhalt.
	 * @param isFinal Gibt an, ob es sich bei diesem Zustand um einen Endzustand handelt.
	 */
	public State(PayloadType payload, boolean isFinal)
	{
		this();
		_payload = payload;	
		if (isFinal) _type = StateType.FINITE;
	}

}
