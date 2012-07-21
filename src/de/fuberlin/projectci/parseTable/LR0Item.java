package de.fuberlin.projectci.parseTable;

import de.fuberlin.commons.util.EasyComparableObject;
import de.fuberlin.projectci.grammar.Production;
import de.fuberlin.projectci.grammar.Symbol;

/**
 * Repräsentiert ein LR0Item, d.h. den "Punkt" innerhalb einer Produktion.
 */
public class LR0Item extends EasyComparableObject{
 
	/**
	 * Die Produktion, auf die sich dieses LRItem bezieht
	 */
	private Production production;
	/** Der Index, wo der 'Punkt' steht.
	 * Invariante: { 0<=index<=production.getRhs().size() }
	 */
	private int index;
	
	public LR0Item(Production production, int index) {	
		if (index<0 || index>production.getRhs().size()){
			throw new IllegalArgumentException("Illegal index "+index+" for production "+production);
		}
		this.production = production;
		this.index = index;		
	}

	@Override
	protected Object[] getSignificantFields() {
		return new Object[]{production, index, getClass()};
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
	
	@Override
	public String toString() {
		StringBuffer strBuf=new StringBuffer();
		strBuf.append(production.getLhs());
		strBuf.append(" -->");
		int i=0;
		for (Symbol aSymbol : production.getRhs()) {
			if (index==i){
				strBuf.append(" \u00b7");
			}
			strBuf.append(" ");
			strBuf.append(aSymbol);
			i++;
		}
		if (index==i){
			strBuf.append(" \u00b7");
		}
		return strBuf.toString();
	}

	/**
	 * 
	 * @return das Symbol nach dem 'Punkt' - oder null, wenn der 'Punkt' am Ende steht
	 */
	public Symbol getNextSymbol(){
		if (index==production.getRhs().size()){
			// Punkt steht am Ende
			return null;
		}
		else{
			return production.getRhs().get(index);
		}
	}
}
 
