package de.fuberlin.projectci.parseTable;

/**
 * Repräsentiert die Error-Action eines LRParsers-Automaten
 */
public class ErrorAction extends Action {
	
	@Override
	protected Object[] getSignificantFields() {
		return new Object[]{getClass()};
	}
	
	@Override
	public String toString() {
		return "error";
	}
}
 
