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

package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar;

import javax.xml.ws.soap.Addressing;

import de.fuberlin.bii.utils.Test;


/**
 * Stellt ein Terminalsymbol dar.
 * 
 * @author Johannes Dahlke
 *
 * @param <Symbol>
 */
@SuppressWarnings("rawtypes")
public class Terminal<T extends Symbol> extends RuleElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T symbol;
	
	
	public Terminal( T symbol) {
		super();
		this.symbol = symbol;
	}
	
	
	@Override
	public boolean equals( Object theOtherObject) {
		
		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !( theOtherObject instanceof Terminal))
			return false;
		
		Terminal theOtherTerminal = (Terminal) theOtherObject;
		
		if ( !theOtherTerminal.getSymbol().equals( this.symbol))
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 5;
		hashCode = 31 * hashCode + symbol.hashCode(); 	
		
		return hashCode;
	}

	
	
	public T getSymbol() {
		return symbol;
	}
	
	@Override
	public String toString() {
		return symbol.toString();
	}


	@SuppressWarnings("unchecked")
	public int compareTo(RuleElement o) {
		if ( Test.isUnassigned(o))
			return 1;
		if ( o instanceof Nonterminal)
			return -1;
		if ( o instanceof Terminator)
			return -1;
		if ( o instanceof EmptyString)
			return +1;
		if ( o instanceof Terminal)
			return ((Terminal)o).getSymbol().compareTo( this.symbol);
		
		return -1;
	}

}
