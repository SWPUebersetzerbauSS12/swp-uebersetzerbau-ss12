package de.fuberlin.projectci.parseTable;

import de.fuberlin.projectci.grammar.Production;

public class ReduceAction extends Action {
	private Production production;
	
	 
	public ReduceAction(Production production) {
		this.production = production;
	}


	public Production getProduction() {
		return production;
	}


	
	 
}
 
