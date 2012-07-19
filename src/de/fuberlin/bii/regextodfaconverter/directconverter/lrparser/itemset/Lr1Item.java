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

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElement;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElementArray;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElementSequenz;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminator;

/**
 * Ein spezielles {@link Item} für die {@link Lr1Closure}.
 * 
 * @author Johannes Dahlke
 *
 */
@SuppressWarnings("rawtypes")
public class Lr1Item extends Item {



	/**
	 * 
	 */
	private static final long serialVersionUID = -2299113205108459441L;
	private Terminal lookahead = new Terminator();
	
	public Lr1Item( ProductionRule productionRule, int analysePosition, Terminal lookahead) {
		super( productionRule, analysePosition);
		this.lookahead = lookahead;	
	}

	public Lr1Item( Nonterminal leftRuleSide, RuleElementSequenz rightRuleSide, Terminal lookahead) {
		super( leftRuleSide, rightRuleSide);
		this.lookahead = lookahead;
	}
	
	public Lr1Item( Nonterminal leftRuleSide, RuleElement[] rightRuleSideElements, Terminal lookahead) {
		super( leftRuleSide, rightRuleSideElements);
		this.lookahead = lookahead;	
	}
	
	public Lr1Item( Nonterminal leftRuleSide, RuleElementSequenz rightRuleSide, Terminal lookahead, int analysePosition) {
		super( leftRuleSide, rightRuleSide, analysePosition);
		this.lookahead = lookahead;
	}

	public Lr1Item( Lr1Item item, Terminal lookahead, int analysePosition) {
		super( item.getLeftRuleSide(), item.getRightRuleSide(), analysePosition);
		this.lookahead = lookahead;
	}




	@Override
	public boolean equals( Object theOtherObject) {
		if ( !( super.superEquals( theOtherObject)))
			return false;
		
		if ( !(theOtherObject instanceof Lr1Item))
			return false;
		
		Lr1Item theOtherItem = (Lr1Item) theOtherObject;
		
		return theOtherItem.getAnalysePosition() == this.getAnalysePosition()
				&& lookahead.equals( theOtherItem.lookahead);
	}
	
	public int hashCode() {
		int hashCode = super.hashCode();
		hashCode = 31 * hashCode + getAnalysePosition(); 	
		hashCode = 31 * hashCode + lookahead.hashCode(); 
		
		return hashCode;
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
		result += ", " + lookahead;
		return result;
	}
	
	/**
	 * Liefert die Sequenz der noch verbleibenden Regelelemente rechts von der aktuellen Analyseposition. 
	 * @return
	 */
	public RuleElementSequenz getSequenzLeftAfterNextRuleElement() {
	  RuleElementSequenz result = new RuleElementArray();
	  for ( int i = getAnalysePosition()+1; i < this.rightSideRuleSize(); i++) {
			result.add( this.getRightRuleSide().get( i));
		}
	  return result;
	}
	
	
	/**
	 * Liefert das Lookahead-Terminal.
	 * @return
	 */
	public Terminal getLookahead() {
		return lookahead;
	}

}
