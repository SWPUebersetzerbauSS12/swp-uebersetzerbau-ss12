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

package de.fuberlin.bii.regextodfaconverter.fsm;

import java.io.Serializable;

/**
 * Stellt einen Übergang eines endlicher Automaten (bzw. einer Zustandsmaschine)
 * dar.
 * 
 * @author Daniel Rotar
 * 
 * @param <ConditionType>
 *            Der Typ der Bedingung für den Zustandsübergang.
 * @param <StatePayloadType>
 *            Der Typ des Inhalts der Zustände.
 */
public class Transition<ConditionType extends Serializable, StatePayloadType extends Serializable> implements Serializable {

	/**
	 * UID für die Serialisierung/Abspeicherung als *.dfa 
	 */
	private static final long serialVersionUID = -1604382036508327591L;
	
	/**
	 * Die Bedingung für den Zustandsübergang (null für einen Epsilon-Übergang).
	 */
	private ConditionType _condition;
	/**
	 * Der Folgezustand.
	 */
	private State<ConditionType, StatePayloadType> _state;

	/**
	 * Gibt die Bedingung für den Zustandsübergang zurück.
	 * 
	 * @return Die Bedingung für den Zustandsübergang (null für einen
	 *         Epsilon-Übergang).
	 */
	public ConditionType getCondition() {
		return _condition;
	}

	/**
	 * Setzt die Bedingung für den Zustandsübergang fest.
	 * 
	 * @param condition
	 *            Die Bedingung für den Zustandsübergang (null für einen
	 *            Epsilon-Übergang).
	 */
	protected void setCondition(ConditionType condition) {
		_condition = condition;
	}

	/**
	 * Gibt den Folgezustand zurück.
	 * 
	 * @return Der Folgezustand.
	 */
	public State<ConditionType, StatePayloadType> getState() {
		return _state;
	}

	/**
	 * Setzt den Folgezustand fest.
	 * 
	 * @param state
	 *            Der Folgezustand.
	 */
	protected void setState(State<ConditionType, StatePayloadType> state) {
		_state = state;
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) 
    	{
	    	return true;
    	}
	    
	    if (o == null)
	    {
	    	return false;
	    }
	    
	    if (!(o instanceof Transition<?,?>))
    	{
	    	return false;
    	} 
	    else 
	    {
	    	Transition<?,?> t = (Transition<?,?>)o;
	    	if (t.getCondition() != null && getCondition() != null)
	    	{
		    	if (t.getCondition().equals(getCondition()) && t.getState().equals(getState())) {
		    		return true;
		    	}
		    	else
		    	{
		    		return false;
		    	}
	    	}
	    	else
	    	{
		    	if (t.getCondition() == null && getCondition() == null && t.getState().equals(getState())) {
		    		return true;
		    	}
		    	else
		    	{
		    		return false;
		    	}
	    	}
	    }
	}
	
	public int hashCode() 
	{ 
		if (getCondition() != null)
		{
			return getCondition().hashCode(); 
		}
		else
		{
			return 0;
		}
	}

	/**
	 * Erstellt ein neues Transition Objekt.
	 * 
	 * @param condition
	 *            Die Bedingung für den Zustandsübergang (null für einen
	 *            Epsilon-Übergang).
	 * @param state
	 *            Der Folgezustand.
	 */
	public Transition(ConditionType condition,
			State<ConditionType, StatePayloadType> state) {
		setCondition(condition);
		setState(state);
	}

}
