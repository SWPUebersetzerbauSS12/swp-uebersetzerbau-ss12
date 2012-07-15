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
import java.util.Iterator;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.EmptyString;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElement;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElementArray;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElementSequenz;
import de.fuberlin.bii.utils.Test;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public abstract class Item extends ProductionRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5146432627519920922L;
	
	private int analysePosition = 0;
	RuleElementSequenz simplifiedRightRuleSide = null;

	public Item( ProductionRule productionRule, int analysePosition) {
		super( productionRule.getLeftRuleSide(), productionRule.getRightRuleSide());
		this.analysePosition = analysePosition;
	}
	
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
	
	@Override
	public Iterator<RuleElement> iterator() {
		return getSimplifiedRightRuleSide().iterator();
	}
	
	
	public boolean IsProcessed() {
		return this.rightSideRuleSize() <= analysePosition;
	}
	
	public boolean canStepForward() {
		return this.rightSideRuleSize() > analysePosition;
	}

	protected RuleElementSequenz getSimplifiedRightRuleSide() {
		if ( Test.isAssigned( simplifiedRightRuleSide))
			return simplifiedRightRuleSide;
	  
		simplifiedRightRuleSide = new RuleElementArray();
		for (RuleElement ruleElement : getRightRuleSide()) {
			if ( !( ruleElement instanceof EmptyString))
				simplifiedRightRuleSide.add( ruleElement);
		}
		return simplifiedRightRuleSide;
	}
	
	public void stepForward() {
		if ( canStepForward())
			analysePosition++;
	}
	
	public RuleElement peekNextRuleElement() {
		if ( canStepForward())
		  return this.getSimplifiedRightRuleSide().get( analysePosition);
		return null;
	}
	
	public RuleElement getNextRuleElementAndStepForward() {
		if ( canStepForward())
		  return this.getSimplifiedRightRuleSide().get( analysePosition++);
		return null;
	}
	
	@Override
	public abstract boolean equals( Object theOtherObject);
	
	protected boolean superEquals( Object theOtherObject) {
		return super.equals( theOtherObject);
	}
	

	public ProductionRule toProduction() {
		return new ProductionRule(getLeftRuleSide(), getRightRuleSide());
	}

	@Override
	public abstract String toString();
	

}
