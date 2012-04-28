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

package regextodfaconverter.fsm;

import tokenmatcher.TokenType;


/**
 * Implementiert  Interface {@link tokenmatcher.StatePayload} 
 * @author Johannes Dahlke
 * @author Daniel Rotar
 */
public class StatePayload implements tokenmatcher.StatePayload {

	private TokenType tokenType;

	private int backsteps = 0;

	/**
	 * Die Priorität dieses Objekts (je größer die Zahl desto größer die Priorität).
	 */
	private int priority = 0;
	
	public StatePayload(TokenType tokenType, int backsteps, int priority) {
		super();
		this.tokenType = tokenType;
		this.backsteps = backsteps;
		this.priority = priority;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public int getBacksteps() {
		return backsteps;
	}
	
	/**
	 * Gibt die Priorität dieses Objekts zurück. 
	 * @return Die Priorität dieses Objekts (je größer die Zahl desto größer die Priorität).
	 */
	public int getPriority()
	{
		return priority;
	}
	
	/**
	 * Setzt die Priorität dieses Objekts fest. 
	 * @param priority Die Priorität dieses Objekts (je größer die Zahl desto größer die Priorität).
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
	
	@Override
	public String toString()
	{
		return tokenType.toString();
	}
}
