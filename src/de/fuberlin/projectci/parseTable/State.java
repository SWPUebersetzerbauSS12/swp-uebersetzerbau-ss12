package de.fuberlin.projectci.parseTable;

import de.fuberlin.commons.util.EasyComparableObject;

public class State extends EasyComparableObject{
	// XXX [Dustin] Ist es wirklich notwendig hier einen String zu nehmen? Warum nicht int/Integer?
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
