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

package de.fuberlin.bii.regextodfaconverter.fsm;

import de.fuberlin.bii.tokenmatcher.attributes.Attribute;
import de.fuberlin.bii.utils.Test;

/**
 * Implementiert Interface {@link tokenmatcher.StatePayload}
 * 
 * @author Johannes Dahlke
 * @author Daniel Rotar
 */
public class StatePayload implements de.fuberlin.bii.tokenmatcher.StatePayload {

	/**
	 * UID für die Serialisierung/Abspeicherung als *.dfa
	 */
	private static final long serialVersionUID = 6562577552654605535L;

	private String tokenType;

	private Attribute attribute;

	/**
	 * Die Priorität dieses Objekts (je größer die Zahl desto größer die
	 * Priorität).
	 */
	private int priority = 0;

	public StatePayload(String tokenType, Attribute attribute, int priority) {
		super();
		this.tokenType = tokenType;
		this.attribute = attribute;
		this.priority = priority;
	}

	/**
	 * Erstellt ein Payload-Objekt mit Standardpriorität
	 * 
	 * @param tokenType
	 */
	public StatePayload(String tokenType, Attribute attribute) {
		super();
		this.tokenType = tokenType;
		this.attribute = attribute;
		this.priority = 0;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * Gibt die Priorität dieses Objekts zurück.
	 * 
	 * @return Die Priorität dieses Objekts (je größer die Zahl desto größer die
	 *         Priorität).
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Setzt die Priorität dieses Objekts fest.
	 * 
	 * @param priority
	 *            Die Priorität dieses Objekts (je größer die Zahl desto größer
	 *            die Priorität).
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/**
	 * Definiert die Gleichheit zweier Payload-Elemente
	 * 
	 * @Author Johannes Dahlke 
	 */
	@Override
	public boolean equals( Object obj) {
		if ( Test.isUnassigned( obj))
			return false;
		
		if ( !( obj instanceof StatePayload))
			return false;
		
		StatePayload theOtherStatePayload = (StatePayload) obj;
		
		boolean isEqual = 
				this.tokenType == theOtherStatePayload.tokenType
		    && this.attribute == theOtherStatePayload.attribute
		    && this.priority == theOtherStatePayload.priority;
		
		return isEqual; 
	}
	
	/**
	 * Definiert den Hashcode des Exemplares
	 * 
	 * @Author Johannes Dahlke 
	 */
	@Override
	public int hashCode() 
	{ 
		int hashCode = 5;
		hashCode = 37 * hashCode + tokenType.hashCode();
		hashCode = 37 * hashCode + attribute.hashCode();
		hashCode = 37 * hashCode + priority;
		
		return hashCode;
	}
	

	@Override
	public String toString() {
		return getTokenType().toString() + ", " + getAttribute().toString()
				+ " @priority " + getPriority();
	}
}
