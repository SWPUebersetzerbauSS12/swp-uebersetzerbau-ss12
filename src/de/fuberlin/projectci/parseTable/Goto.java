package de.fuberlin.projectci.parseTable;

public class Goto {

	private State targetState;

	public Goto(State targetState) {
		this.targetState = targetState;
	}

	public State getTargetState() {
		return targetState;
	}

}
