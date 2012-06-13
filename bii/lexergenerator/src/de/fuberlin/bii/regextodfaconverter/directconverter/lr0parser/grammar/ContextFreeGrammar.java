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
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Sets;
import utils.Test;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public class ContextFreeGrammar extends ProductionMap implements Grammar {

	private Nonterminal startSymbol = null;

	private Set<Nonterminal> nonterminals = new HashSet<Nonterminal>();
	private Set<Terminal> terminals = new HashSet<Terminal>();

	public Nonterminal getStartSymbol() {
		return startSymbol;
	}
	
	
	public void setStartSymbol( Nonterminal startSymbol) {
		this.startSymbol = startSymbol;
	}

	@Override
	public boolean addProduction(ProductionRule productionRule) {
		if (Test.isUnassigned(productionRule))
			return false;

		Nonterminal leftRuleSide = productionRule.getLeftRuleSide();
		if (Test.isUnassigned(startSymbol))
			startSymbol = leftRuleSide;

		terminals.addAll(productionRule.getTerminalSet());
		nonterminals.addAll(productionRule.getNonterminalSet());

		RuleElementSequenz rightRuleSide = productionRule.getRightRuleSide();
		if (this.containsKey(leftRuleSide)) {
			this.get(leftRuleSide).add(rightRuleSide);
		} else {
			HashSet<RuleElementSequenz> ruleSet = new HashSet<RuleElementSequenz>();
			ruleSet.add(rightRuleSide);
			this.put(leftRuleSide, ruleSet);
		}
		return true;
	}

	public Set<ProductionRule> getProductions() {
		Set<ProductionRule> result = new HashSet<ProductionRule>();
		for (Nonterminal nonterminal : this.keySet()) {
			for (RuleElementSequenz ruleElementSequenz : this.get(nonterminal)) {
				result.add(new ProductionRule(nonterminal, ruleElementSequenz));
			}
		}
		return result;
	}

	private Set<Terminal> getFirstSetOfRuleElement(RuleElement ruleElement, HashMap<Nonterminal, Set<Terminal>> growingFirstSetTable) {

		Set<Terminal> firstSet = new HashSet<Terminal>();

		if (ruleElement instanceof EmptyString) {
			firstSet.add((EmptyString) ruleElement);
			return firstSet;
		}

		if (ruleElement instanceof Terminal) {
			firstSet.add((Terminal) ruleElement);
			return firstSet;
		}

		// otherwise ruleElement is an instance of Nonterminal
		assert ruleElement instanceof Nonterminal;
		Nonterminal nonterminal = (Nonterminal) ruleElement;

		if (Test.isUnassigned(growingFirstSetTable))
			growingFirstSetTable = new HashMap<Nonterminal, Set<Terminal>>();

		if (growingFirstSetTable.containsKey(nonterminal))
			return growingFirstSetTable.get(nonterminal);

		growingFirstSetTable.put(nonterminal, new HashSet<Terminal>());

		// we test for each rule defined for this nonterminal
		for (RuleElementSequenz rightRuleSide : get(nonterminal)) {
			ProductionRule currentProcessedRule = new ProductionRule(nonterminal, rightRuleSide);
			for (RuleElement rightRuleSideElement : rightRuleSide) {

				Set<Terminal> firstSetOfCurrentElement = getFirstSetOfRuleElement(rightRuleSideElement, growingFirstSetTable);
				// update table
				if (rightRuleSideElement instanceof Nonterminal) {
					growingFirstSetTable.put((Nonterminal) rightRuleSideElement, firstSetOfCurrentElement);
				}

				firstSet = Sets.unionCollections(firstSet, firstSetOfCurrentElement);

				if (!firstSetOfCurrentElement.contains(new EmptyString()))
					break;
			}
		}

		return firstSet;
	}

	private <K, V> Map<K, Set<V>> removeEmptySetsFromMap(Map<K, Set<V>> map) {
		Set<K> keySet = new HashSet<K>(map.keySet());
		for (K key : keySet) {
			Set<V> currentSet = map.get(key);
			if (currentSet != null)
				if (currentSet.isEmpty())
					map.remove(key);
		}
		return map;
	}

	public HashMap<Nonterminal, Set<Terminal>> getFirstSets() {
		HashMap<Nonterminal, Set<Terminal>> result = new HashMap<Nonterminal, Set<Terminal>>();
		// determine firstset over all productions of this grammar
		for (Nonterminal nonterminal : this.keySet()) {
			Set<Terminal> currentFirstSet = getFirstSetOfRuleElement(nonterminal, result);
			result.put(nonterminal, currentFirstSet);
			removeEmptySetsFromMap(result);
		}
		return result;
	}

	public HashMap<Nonterminal, Set<Terminal>> getFollowSets() {
		boolean nothingAddedAnymore;
		HashMap<Nonterminal, Set<Terminal>> result = new HashMap<Nonterminal, Set<Terminal>>();

		// add $ in followset of start symbol
		HashSet<Terminal> followSetOfStartSymbol = new HashSet<Terminal>();
		followSetOfStartSymbol.add(new Terminator());
		result.put(this.getStartSymbol(), followSetOfStartSymbol);

		do {
			nothingAddedAnymore = true;
			for (Nonterminal nonterminal : this.keySet()) {
				Set<Terminal> followSetOfCurrentNonterminal = getFollowSetOfRuleElement(nonterminal, result);
				Set<Terminal> presentFollowSetOfCurrentNonterminal = result.get(nonterminal);
				boolean elementsAdded = !(Test.isAssigned(presentFollowSetOfCurrentNonterminal) 
						&& presentFollowSetOfCurrentNonterminal.containsAll(followSetOfCurrentNonterminal));
				if (elementsAdded) {
					followSetOfCurrentNonterminal = Sets.unionCollections(followSetOfCurrentNonterminal, presentFollowSetOfCurrentNonterminal);
					result.put(nonterminal, followSetOfCurrentNonterminal);
				}
				nothingAddedAnymore &= !elementsAdded;
			}
		} while (!nothingAddedAnymore);
		return result;
	}


		private Set<Terminal> getFollowSetOfRuleElement(Nonterminal thisNonterminal, HashMap<Nonterminal, Set<Terminal>> growingFollowSetTable) {
			Set<Terminal> result = new HashSet<Terminal>();
			EmptyString emptyString = new EmptyString();
			boolean isLastElementInSequenz = true;

			// lookup in each rule for occurences of the given thisNonterminal
			for (Nonterminal nonterminal : this.keySet()) {
			    // lookup in each rule for nonterminal for occurences of the given thisNonterminal
				for (RuleElementSequenz ruleElementSequenz : this.get(nonterminal)) {
					boolean startAccumulateFirstSet = false;
					// therefore scan each element of the right rule side
					for (int i = 0; i < ruleElementSequenz.size(); i++) {
						isLastElementInSequenz = false;
						   
						// we have to decide two cases
						// 1. A -> aBb  =>  (FIRST(b) / {\epsilon}) \in FOLLOW(B)
						// 2. A -> aB  or  A -> aBb mit b=\epsilon   =>  FOLLOW(A) \subset FOLLOW(B)
						// In both cases, we move forward until we read B or reaches the end of rule
						RuleElement ruleElement = ruleElementSequenz.get(i);
						if ( !startAccumulateFirstSet) {
							startAccumulateFirstSet |= ruleElement.equals(thisNonterminal);
							isLastElementInSequenz = true; // forehanded set to true
							continue;
						}
						

						// we determine and accumulate the firstsets of the following elements until
						// we read no more \epsilon
						Set<Terminal> currentFirstSet = getFirstSetOfRuleElement(ruleElement, null);
                        if (currentFirstSet.contains(emptyString)) {
						   // we've read an \epsilon
                        	if (ruleElementSequenz.size() > i + 1) {
                        		// but there are more candidates to deliver a further \epsilon, so we remove it
								currentFirstSet.remove(emptyString);
							} else {
								// otherwise we have case 2. So we add FOLLOW(A) 
								currentFirstSet.addAll(growingFollowSetTable.get(thisNonterminal));
							}
							result = Sets.unionCollections(result, currentFirstSet);
						} else {
							// else there is no \epsilon in b so we simply add the set to result
							result = Sets.unionCollections(result, currentFirstSet);
							// and interrupt the accumulation of the firstsets. But continue with scanning. 
							// There could be another occurence of thisNonerminal    
							startAccumulateFirstSet = ruleElement.equals(thisNonterminal);
							if ( startAccumulateFirstSet)
								isLastElementInSequenz = true; // forehanded set to true
							continue;
						}

					}
					

					// add Follow(A) to Follow(B) if A -> aB
					if (isLastElementInSequenz 
							&& startAccumulateFirstSet) {
						result = Sets.unionCollections(result, growingFollowSetTable.get(nonterminal));
					}

				}
			}

		return result;
	}

	public Set<Terminal> getTerminals() {
		return terminals;
	}

	public Set<Nonterminal> getNonterminals() {
		return nonterminals;
	}

}
