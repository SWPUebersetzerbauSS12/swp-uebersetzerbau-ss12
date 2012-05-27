package main.model;

import java.util.LinkedList;

public class Variable {
	String type;
	LinkedList<Address> addresses;
	int size;

	public Variable() {
		this("undefined");
	}

	public Variable(String type, int size, int stackAddress) {
		this(type);
		this.size = size;
		addresses.add(new StackAddress(stackAddress));
	}

	public Variable(String type, RegisterAddress sum) {
		this(type);
		if (this.type.equals("i32"))
			this.size = 4;
		else
			this.size = 4;
		addresses.add(sum);
	}

	public Variable(String type) {
		this.type = type;
		addresses = new LinkedList<Address>();
	}

	public int getSize() {
		return size;
	}

	public String getAddress() {
		return addresses.getFirst().getFullName();
	}
}
