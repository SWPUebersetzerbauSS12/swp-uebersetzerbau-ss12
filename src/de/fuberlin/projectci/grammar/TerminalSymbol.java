package de.fuberlin.projectci.grammar;

public class TerminalSymbol extends Symbol {

	public TerminalSymbol(String value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return "\""+getName()+"\"";
	}
 
}
 
