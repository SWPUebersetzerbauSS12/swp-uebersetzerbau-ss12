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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.fuberlin.bii.utils.Test;


/**
 * Stellt eine Produktionsregel dar.
 * 
 * @author Johannes Dahlke
 *
 */
@SuppressWarnings("rawtypes")
public class ProductionRule implements Iterable<RuleElement>, Serializable {

	private static final long serialVersionUID = 652665932709829362L;

	private Nonterminal leftRuleSide;

	private RuleElementSequenz rightRuleSide;

	public ProductionRule(Nonterminal leftRuleSide, RuleElementSequenz rightRuleSide) {
		super();
		this.leftRuleSide = leftRuleSide;
		if (Test.isAssigned(rightRuleSide) && rightRuleSide.size() > 0) {
			this.rightRuleSide = filterEmptyStrings(rightRuleSide);
		} else {
			this.rightRuleSide = new RuleElementArray();
			this.rightRuleSide.add(new EmptyString());
		}
	}

	public ProductionRule(Nonterminal leftRuleSide, final RuleElement... rightRuleSideElements) {
		this(leftRuleSide, new RuleElementArray() {

			private static final long serialVersionUID = -8519909723287300646L;

			RuleElementArray getFilledArray() {
				RuleElementArray ruleElementArray = new RuleElementArray();
				ruleElementArray.addAll(Arrays.asList(rightRuleSideElements));
				return ruleElementArray;
			}
		}.getFilledArray());
	}

	private static RuleElementSequenz filterEmptyStrings(RuleElementSequenz elementSequenz) {
		int len = elementSequenz.size();
		for (int i = len - 1; i > 0; i--) {
			if (elementSequenz.get(i) instanceof EmptyString)
				elementSequenz.remove(i);
		}
		return elementSequenz;
	}

	/**
	 * Liefert die linke Regelseite.
	 * @return
	 */
	public Nonterminal getLeftRuleSide() {
		return leftRuleSide;
	}

	protected boolean isRightSideRuleEmpty() {
		boolean result = true;
		for (RuleElement ruleElement : rightRuleSide) {
			result &= (ruleElement instanceof EmptyString);
		}
		return result;
	}

	protected int rightSideRuleSize() {
		int result = 0;
		for (RuleElement ruleElement : rightRuleSide) {
			result += (ruleElement instanceof EmptyString) ? 0 : 1;
		}
		return result;
	}

	/**
	 * Liefert die rechte Regelseite.
	 * 
	 * @return
	 */
	public RuleElementSequenz getRightRuleSide() {
		return rightRuleSide;
	}

	public Iterator<RuleElement> iterator() {
		return rightRuleSide.iterator();
	}

	/**
	 * Liefert die Menge der Elemente auf der rechten Regelseite.
	 * @return
	 */
	public Set<RuleElement> getRuleElementSet() {
		Set<RuleElement> result = new HashSet<RuleElement>();
		result.add(leftRuleSide);
		for (RuleElement ruleElement : this) {
			result.add(ruleElement);
		}
		return result;
	}
	
	/**
	 * Liefert die Menge aller Terminale der rechten Regelseite.
	 * @return
	 */
	public Set<Terminal> getTerminalSet() {
		Set<Terminal> result = new HashSet<Terminal>();
		for (RuleElement ruleElement : this) {
			if ( ruleElement instanceof Terminal)
				result.add((Terminal)ruleElement);
		}
		return result;
	}

	/**
	 * Liefert die Menge aller Nichtterminale der rechten Regelseite sowie der linken Regelseite.
	 * @return
	 */
	public Set<Nonterminal> getNonterminalSet() {
		Set<Nonterminal> result = new HashSet<Nonterminal>();
		result.add(leftRuleSide);
		for (RuleElement ruleElement : this) {
			if ( ruleElement instanceof Nonterminal)
				result.add((Nonterminal)ruleElement);
		}
		return result;
	}

	@Override
	public boolean equals(Object theOtherObject) {

		if (Test.isUnassigned(theOtherObject))
			return false;

		if (!(theOtherObject instanceof ProductionRule))
			return false;

		ProductionRule theOtherProductionRule = (ProductionRule) theOtherObject;

		if (!theOtherProductionRule.getLeftRuleSide().equals(this.leftRuleSide))
			return false;

		if (!theOtherProductionRule.getRightRuleSide().equals( this.rightRuleSide))
			return false;

		return true;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 5;
		hashCode = 31 * hashCode + leftRuleSide.hashCode();
		hashCode = 31 * hashCode + rightRuleSide.hashCode();
		
		return hashCode;
	}
	
	@Override
	public String toString() {
		String result = getLeftRuleSide().toString() + " -> ";
		for( int i = 0; i < getRightRuleSide().size(); i++) {
			result += getRightRuleSide().get(i).toString();
		}			
		return result;
	}


}
