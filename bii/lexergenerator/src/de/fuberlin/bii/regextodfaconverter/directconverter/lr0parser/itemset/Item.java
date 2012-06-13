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

package de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.itemset;

import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElementSequenz;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public class Item extends ProductionRule {

	private int analysePosition = 0;
	
	public Item( Nonterminal leftRuleSide, RuleElementSequenz rightRuleSide) {
		super( leftRuleSide, rightRuleSide);
    }
	
	public Item( Nonterminal leftRuleSide,
			RuleElement ... rightRuleSideElements) {
		super( leftRuleSide, rightRuleSideElements);
	}
	
	public Item( Nonterminal leftRuleSide, RuleElementSequenz rightRuleSide, int analysePosition) {
		super( leftRuleSide, rightRuleSide);
        this.analysePosition = analysePosition;
	}
	
	public Item( Item item, int analysePosition) {
		super( item.getLeftRuleSide(), item.getRightRuleSide());
		this.analysePosition = analysePosition;
    }
	
	public int getAnalysePosition() {
		return analysePosition;
	}
	
	
	public void setAnalysePosition( int analysePosition) {
		this.analysePosition = analysePosition;
	}
	
	
	
	public boolean IsProcessed() {
		return this.rightSideRuleSize() <= analysePosition;
	}
	
	public boolean canStepForward() {
		return this.rightSideRuleSize() > analysePosition;
	}

	
	public void stepForward() {
		if ( canStepForward())
			analysePosition++;
	}
	
	public RuleElement peekNextRuleElement() {
		if ( canStepForward())
		  return this.getRightRuleSide().get( analysePosition);
		return null;
	}
	
	public RuleElement getNextRuleElementAndStepForward() {
		if ( canStepForward())
		  return this.getRightRuleSide().get( analysePosition++);
		return null;
	}
	
	@Override
	public boolean equals( Object theOtherObject) {
		if ( !super.equals( theOtherObject))
			return false;
		
		if ( !(theOtherObject instanceof Item))
			return false;
		
		Item theOtherItem = (Item) theOtherObject;
		
		return theOtherItem.getAnalysePosition() == this.analysePosition;
	}

	public ProductionRule toProduction() {
		return new ProductionRule(getLeftRuleSide(), getRightRuleSide());
	}

	@Override
	public String toString() {
		String result = getLeftRuleSide().toString() + " -> ";
		if ( analysePosition == 0)
			result += ".";
		for( int i = 0; i < getRightRuleSide().size(); i++) {
			result += getRightRuleSide().get(i).toString();
			if ( analysePosition == i+1)
				result += ".";
		}			
		return result;
	}
	

}
