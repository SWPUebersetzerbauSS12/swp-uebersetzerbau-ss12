package de.fuberlin.projectci.parseTable;

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
 
