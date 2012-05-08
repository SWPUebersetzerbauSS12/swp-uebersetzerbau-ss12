package de.fuberlin.projectci.parseTable;

import de.fuberlin.projectci.grammar.Production;

public class LR0Item {
 
	/**
	 * Die Produktion, auf die sich dieses LRItem bezieht
	 */
	private Production production;
	/** Der Index, wo der 'Punkt' steht.
	 * Invariante: { 0<=index<=production.getRhs().size() }
	 */
	private int index;
	
	public LR0Item(Production production, int index) {	
		this.production = production;
		this.index = index;		
	}

	public int getIndex() {
		return index;
	}

	public Production getProduction() {
		return production;
	}

	/*
	 * Soll anzeigen, ob das LRItem ein Kernel-Item ist oder nicht.
	 * Bin mir aber noch unsicher, ob das beim Erzeugen mitgeteilt, oder on-the-fly berechnet werden sollte.
	 * 
	 */
	public boolean isKernelItem() {
		return false;
	}
	 
}
 
