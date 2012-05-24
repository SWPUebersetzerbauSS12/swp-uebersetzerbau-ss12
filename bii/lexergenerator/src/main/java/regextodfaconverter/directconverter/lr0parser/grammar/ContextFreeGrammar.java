package regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Sets;
import utils.Test;

public class ContextFreeGrammar extends ProductionMap {

	private Nonterminal startSymbol = null;

	public Nonterminal getStartSymbol() {
		return startSymbol;
	}

	@Override
	public boolean addProduction(ProductionRule productionRule) {
		if (Test.isUnassigned(productionRule))
			return false;

		Nonterminal leftRuleSide = productionRule.getLeftRuleSide();
		if (Test.isUnassigned(startSymbol))
			startSymbol = leftRuleSide;

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

	HashMap<Nonterminal, Set<Terminal>> getFollowSets(Terminal terminatorTerminal) {
		boolean nothingAddedAnymore;
		HashMap<Nonterminal, Set<Terminal>> result = new HashMap<Nonterminal, Set<Terminal>>();

		// add $ in followset of start symbol
		HashSet<Terminal> followSetOfStartSymbol = new HashSet<Terminal>();
		followSetOfStartSymbol.add(terminatorTerminal);
		result.put(this.getStartSymbol(), followSetOfStartSymbol);

		do {
			nothingAddedAnymore = true;
			for (Nonterminal nonterminal : this.keySet()) {
				Set<Terminal> followSetOfCurrentNonterminal = getFollowSetOfRuleElement(nonterminal, result);
				Set<Terminal> presentFollowSetOfCurrentNonterminal = result.get(nonterminal);
				boolean elementsAdded = !followSetOfCurrentNonterminal.equals(presentFollowSetOfCurrentNonterminal);
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

		for (Nonterminal nonterminal : this.keySet()) {
			boolean isLastElementInSequenz = false;
			for (RuleElementSequenz ruleElementSequenz : this.get(nonterminal)) {
				boolean startAccumulateFirstSet = false;
				for (int i = 0; i < ruleElementSequenz.size(); i++) {
					isLastElementInSequenz = !startAccumulateFirstSet;
					RuleElement ruleElement = ruleElementSequenz.get(i);
					if (ruleElement.equals(thisNonterminal)) {
						startAccumulateFirstSet = true;
					} else if (startAccumulateFirstSet) {
						Set<Terminal> currentFirstSet = getFirstSetOfRuleElement(ruleElement, null);

						if (currentFirstSet.contains(emptyString)) {
							if (ruleElementSequenz.size() > i + 1) {
								currentFirstSet.remove(emptyString);
							} else {
								currentFirstSet.addAll(growingFollowSetTable.get(thisNonterminal));
							}
							result = Sets.unionCollections(result, currentFirstSet);
						} else {
							result = Sets.unionCollections(result, currentFirstSet);
							break;
						}

					}
				}

				// add Follow(A) to Follow(B) if A -> aB
				if (isLastElementInSequenz) {
					result = Sets.unionCollections(result, growingFollowSetTable.get(nonterminal));
				}

			}
		}

		return result;
	}

}
