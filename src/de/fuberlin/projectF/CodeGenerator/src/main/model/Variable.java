package main.model;

import java.util.LinkedList;

public class Variable {
	String type;
	public String name;
	LinkedList<Address> addresses;
	int size;

	public Variable() {
		this("undefined","");
	}

	public Variable(String type, int size, int stackAddress, String name) {
		this(type, name);
		this.size = size;
		this.name = name;
		addresses.add(new StackAddress(stackAddress));
	}

	public Variable(String type, RegisterAddress sum, String name) {
		this(type, name);
		
		if (this.type.equals("i32"))
			this.size = 4;
		else
			this.size = 4;
		addresses.add(sum);
	}

	public Variable(String type, String name) {
		this.type = type;
		this.name = name;
		addresses = new LinkedList<Address>();
	}

	public int getSize() {
		return size;
	}

	public String getAddress() {
		return addresses.getFirst().getFullName();
	}
}
