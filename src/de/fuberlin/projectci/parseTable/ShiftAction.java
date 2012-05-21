package de.fuberlin.projectci.parseTable;

public class ShiftAction extends Action {

	private State targetState;

	public ShiftAction(State targetState) {
		this.targetState = targetState;
	}

	@Override
	protected Object[] getSignificantFields() {
		return new Object[]{targetState, getClass()};
	}
	
	public State getTargetState() {
		return targetState;
	}

	@Override
	public String toString() {
		return "shift "+getTargetState();
	}
}
