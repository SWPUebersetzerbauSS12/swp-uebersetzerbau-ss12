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

package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser;


import java.io.Serializable;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.EmptyString;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElement;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElementSequenz;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Closure;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr0Closure;

/**
 * 
 * @author Johannes Dahlke
 *
 * @param <Element>
 */
@SuppressWarnings("rawtypes")
public class ReduceAction<Element extends Symbol, SpecializedClosure extends Closure> extends Action<Element, SpecializedClosure> implements Serializable {

	private static final long serialVersionUID = -4843681115817022341L;
	private ProductionRule reduceRule;
	
	public ReduceAction( ProductionRule reduceRule) {
		super();
		this.reduceRule = reduceRule;
	}

	public Object handleOnAutomat( ItemAutomatInterior<Element, SpecializedClosure> itemAutomat) throws ReduceException {
		// apply rule elements and reduce reduce the stacks by the way
		RuleElementSequenz rightReduceRuleSide = reduceRule.getRightRuleSide();
		for ( int i = rightReduceRuleSide.size(); i > 0; i--) {
		   RuleElement reduceRuleElement = rightReduceRuleSide.get( i-1);
		   if ( !( reduceRuleElement instanceof EmptyString)) {
		  	 itemAutomat.getClosureStack().pop();
		  	 RuleElement elementFromStack = itemAutomat.getSymbolStack().pop();
		  
         if ( ! elementFromStack.equals( reduceRuleElement))
			     throw new ReduceException(String.format("Missing expected element %s while reduce with rule %s. Found instead %s.", reduceRuleElement, reduceRule, elementFromStack));
		   } else {
		  	 // do nothing to reduce \epsilon
		   }
		}
		itemAutomat.getSymbolStack().push( reduceRule.getLeftRuleSide());
		
		return itemAutomat.getSymbolStack().peek();
	}
	
	public ProductionRule getReduceRule() {
		return reduceRule;
	}
	
	@Override
	public String toString() {
		return "Reduce with rule " + reduceRule.toString();
	}
	

}
