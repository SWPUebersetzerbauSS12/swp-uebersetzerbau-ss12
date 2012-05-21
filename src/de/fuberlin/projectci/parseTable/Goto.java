package de.fuberlin.projectci.parseTable;

import de.fuberlin.commons.util.EasyComparableObject;

public class Goto extends EasyComparableObject{

	private State targetState;

	public Goto(State targetState) {
		this.targetState = targetState;
	}

	@Override
	protected Object[] getSignificantFields() {
		return new Object[]{targetState, getClass()};
	}
	
	public State getTargetState() {
		return targetState;
	}

}
