package de.fuberlin.projectci.grammar;

import de.fuberlin.commons.parser.ISymbol;


public abstract class Symbol implements ISymbol {
	private String name;
	
	public Symbol(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}	

	/*
	 * Die Methoden equals und hashCode müssen unbedingt beide überschrieben werden damit nicht doppelte Symbole
	 * in den Java eigenen Datenstrukturen (HashMap, HashSet, ...) auftauchen können!
	 */
	
	@Override
	public boolean equals(Object obj) {		
		Symbol symbol;
		
		// Typsicheres Casten
		if(obj instanceof Symbol) 
			symbol = (Symbol) obj;
		else
			return false;
		
		if(symbol.name.equals(name))
			return true;
		
		return false;

	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
