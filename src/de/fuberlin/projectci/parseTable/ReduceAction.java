package de.fuberlin.projectci.parseTable;

import de.fuberlin.projectci.grammar.Production;

/**
 * Repräsentiert die Reduce-Action eines LRParsers-Automaten mit der zur reduzierenden Produktion.
 */
public class ReduceAction extends Action {
	private Production production;
	
	 
	public ReduceAction(Production production) {
		this.production = production;
	}

	@Override
	protected Object[] getSignificantFields() {
		return new Object[]{production, getClass()};
	}

	public Production getProduction() {
		return production;
	}


	@Override
	public String toString() {
		return "reduce "+getProduction();
	}
	
	 
}
 
