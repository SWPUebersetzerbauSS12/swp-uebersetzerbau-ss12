package de.fuberlin.projectci.parseTable;

public class State {
	private String name;

	public State(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
