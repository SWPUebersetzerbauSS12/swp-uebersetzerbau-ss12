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

import java.util.ArrayList;
import java.util.List;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr0Item;

/**
 * 
 * @author Johannes Dahlke
 *
 */
@SuppressWarnings("rawtypes")
public class RuleElementArray extends ArrayList<RuleElement> implements RuleElementSequenz {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 330334622181950391L;




	public RuleElementArray() {
	  super();
	}
	
	public RuleElementArray( RuleElementSequenz sequenz) {
	  super( sequenz);
	}
	
	public RuleElementArray( RuleElementSequenz sequenz, RuleElement ... furtherElements) {
	  super( sequenz);
	  for ( RuleElement ruleElement : furtherElements) {
			add( ruleElement);
		}
	}
	
	
	
	
	public static <T extends Symbol> RuleElementArray toRuleElementArray( T ... ts) {
		RuleElementArray result = new RuleElementArray();
		for ( T t : ts) {
			result.add( new Terminal<T>( t));
		}
		return result;
	}
	
	
	@Override
	public boolean equals( Object theOtherObject) {
		
		if ( !(theOtherObject instanceof RuleElementArray))
			return false;
		
		RuleElementArray theOtherArray = (RuleElementArray) theOtherObject;
		
		if (theOtherArray.size() != this.size())
			return false;

		int length = theOtherArray.size();
		for (int i = 0; i < length; i++) {
			if (!theOtherArray.get(i).equals(this.get(i)))
				return false;
		}
		
		return true;
	}
	
	
	@Override
	public int hashCode() {
		int hashCode = 5;
		hashCode = 31 * hashCode + this.size();
		int i = 0;
		for ( RuleElement ruleElement : this) {
			hashCode = 31 * hashCode + ruleElement.hashCode();
			hashCode = 37 * hashCode + i++;		
		}
		
		return hashCode;
	}
	
	
}
