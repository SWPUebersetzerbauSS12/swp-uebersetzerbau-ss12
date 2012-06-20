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

import java.util.HashSet;

import de.fuberlin.bii.utils.Test;


/**
 * 
 * @author Johannes Dahlke
 *
 */
public class ProductionSet extends HashSet<ProductionRule> {
	
	public boolean IsLeftSideUnique() {
		Nonterminal lastNonterminal = null;
		for ( ProductionRule rule : this) {
			if ( lastNonterminal == null) {
				lastNonterminal = rule.getLeftRuleSide();
			} else {
			  if ( !rule.getLeftRuleSide().equals( lastNonterminal)) {
			  	return false;
			  } else {
			    lastNonterminal = rule.getLeftRuleSide(); 	
			  }
			}
		}
		return true;
	}
	
	@Override
	public boolean contains(Object theOtherObject) {

		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !( theOtherObject instanceof ProductionRule))
			return false;
		
		ProductionRule theOtherProductionRule = (ProductionRule) theOtherObject;
		
		for (ProductionRule rule : this) {
			if ( rule.equals(theOtherObject))
				return true;
		}
		return false;
	}

}
