package de.fuberlin.projectci.parseTable;

import de.fuberlin.commons.util.EasyComparableObject;

public class State extends EasyComparableObject{
	private String name;

	public State(String name) {
		this.name = name;
	}

	@Override
	protected Object[] getSignificantFields() {
		return new Object[]{name, getClass()};
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
