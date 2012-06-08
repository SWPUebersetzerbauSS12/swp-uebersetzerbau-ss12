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

package regextodfaconverter.directconverter.lr0parser.grammar;

import utils.Test;


/**
 * 
 * @author Johannes Dahlke
 *
 */
public class Nonterminal extends RuleElement {
	
	private String name = null;
	
	
	public Nonterminal() {
		super();
	}
	
	public Nonterminal( String name) {
		super();
		this.name = name;
	}
	

	@Override
	public boolean equals( Object theOtherObject) {
		
		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !( theOtherObject instanceof Nonterminal))
			return false;
		
		Nonterminal theOtherNonterminal = (Nonterminal) theOtherObject;
		// Wenn über Namen gearbeitet wird, dann gleich, wenn Bezeichner gleich sind
		if ( Test.isAssigned( this.name) && this.name.equals( theOtherNonterminal.name))
			return true;
		
		return super.equals(theOtherNonterminal);
	}
	
	@Override
	public String toString() {
		return name != null ? name : super.toString();
	}
	
	
	public int compareTo(RuleElement o) {
		if ( Test.isUnassigned(o))
			return 1;
		if ( o instanceof Terminal)
			return 1;
		if ( o instanceof Terminator)
			return 1;
		if ( o instanceof EmptyString)
			return 1;
		if ( o instanceof Nonterminal) {
			if ( Test.isAssigned( ((Nonterminal)o).name)
					&& Test.isAssigned( this.name))
			  return ((Nonterminal)o).name.compareTo( this.name);
			else if ( Test.isAssigned( this.name))
			  return 1;
			else if ( Test.isAssigned( ((Nonterminal)o).name))
			  return -1;
			else return 0; // both has no name
		}
		return -1;
	}
	
}
