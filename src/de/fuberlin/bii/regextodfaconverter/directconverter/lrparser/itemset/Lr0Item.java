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

package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset;

import java.io.Serializable;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElement;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElementSequenz;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public class Lr0Item extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 365106322353635423L;


	public Lr0Item( ProductionRule productionRule, int analysePosition) {
		super( productionRule, analysePosition);
	}
	
	public Lr0Item( Nonterminal leftRuleSide, RuleElementSequenz rightRuleSide) {
		super( leftRuleSide, rightRuleSide);
    }
	
	public Lr0Item( Nonterminal leftRuleSide,
			RuleElement ... rightRuleSideElements) {
		super( leftRuleSide, rightRuleSideElements);
	}
	
	public Lr0Item( Nonterminal leftRuleSide, RuleElementSequenz rightRuleSide, int analysePosition) {
		super( leftRuleSide, rightRuleSide, analysePosition);
	}
	
	public Lr0Item( Lr0Item item, int analysePosition) {
		super( item.getLeftRuleSide(), item.getRightRuleSide(), analysePosition);
  }
	
	
	@Override
	public boolean equals( Object theOtherObject) {
		if ( !( super.superEquals( theOtherObject)))
			return false;
		
		if ( !(theOtherObject instanceof Lr0Item))
			return false;
		
		Lr0Item theOtherItem = (Lr0Item) theOtherObject;
		
		return theOtherItem.getAnalysePosition() == this.getAnalysePosition();
	}


	@Override
	public String toString() {
		String result = getLeftRuleSide().toString() + " -> ";
		if ( getAnalysePosition() == 0)
			result += ".";
		for( int i = 0; i < rightSideRuleSize(); i++) {
			result += getSimplifiedRightRuleSide().get(i).toString();
			if ( getAnalysePosition() == i+1)
				result += ".";
		}			
		return result;
	}
	

}
