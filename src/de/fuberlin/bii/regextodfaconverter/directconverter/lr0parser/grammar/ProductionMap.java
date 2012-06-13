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

package de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.HashMap;
import java.util.HashSet;

import de.fuberlin.bii.utils.Test;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public class ProductionMap extends HashMap<Nonterminal, HashSet<RuleElementSequenz>> {
	
		
	public boolean addProduction( ProductionRule productionRule) {
		if ( Test.isUnassigned( productionRule))
			return false;
		
		Nonterminal leftRuleSide = productionRule.getLeftRuleSide();
			
		RuleElementSequenz rightRuleSide = productionRule.getRightRuleSide();
		if ( this.containsKey( leftRuleSide)) {
			this.get( leftRuleSide).add( rightRuleSide);
		} else {
			HashSet<RuleElementSequenz> ruleSet = new HashSet<RuleElementSequenz>();
      ruleSet.add( rightRuleSide);  
			this.put( leftRuleSide, ruleSet);
		}
		return true;
	}
	
	public boolean addAll( ProductionSet productionSetToAdd) {
		boolean result = false;
		for ( ProductionRule productionRule : productionSetToAdd) {
			result |= addProduction( productionRule);
		}
		return result;
	}
	
	
	
}
