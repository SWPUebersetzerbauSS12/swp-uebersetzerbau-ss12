package de.fuberlin.projectF.CodeGenerator.model;

import java.util.ArrayList;

public class Variable {
	public String type;
	public String name;
	ArrayList<RegisterAddress> regAddresses;
	ArrayList<MMXRegisterAddress> mmxAddresses;
	ArrayList<StackAddress> stackAddresses;
	int size;

	public Variable() {
		this("undefined", "");
	}

	public Variable(String type, String name) {
		this.type = type;
		this.name = name;
		regAddresses = new ArrayList<RegisterAddress>();
		mmxAddresses = new ArrayList<MMXRegisterAddress>();
		stackAddresses = new ArrayList<StackAddress>();
		
		if (this.type.equals("i32"))
			this.size = 4;
		else if(this.type.equals("double"))
			this.size = 8;
	}

	public Variable(String type, int size, String name) {
		this(type, name);
		this.size = size;
		this.name = name;
	}

	// Konstruktor für neue Variable mit impliziter Stackadresse
	public Variable(String type, int size, int stackAddress, String name) {
		this(type, name);
		this.size = size;
		this.name = name;
		stackAddresses.add(new StackAddress(stackAddress));
	}

	// Konstruktor für neue Variable mit Registeradresse
	public Variable(String type, RegisterAddress reg, String name) {
		this(type, name);
		regAddresses.add(reg);
	}
	
	public Variable(String type, MMXRegisterAddress reg, String name) {
		this(type, name);
		mmxAddresses.add(reg);
	}

	public void addStackAddress(StackAddress stackAddress) {
		stackAddresses.add(stackAddress);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getAddress() {
		if (!regAddresses.isEmpty())
			return getRegAddress().getFullName();
		else if (!mmxAddresses.isEmpty()) {
			return getRegAddress().getFullName();
		}
		return stackAddresses.get(0).getFullName();
	}
	
	public String getAddress(int offset) {
		if (!regAddresses.isEmpty())
			return getRegAddress().getFullName();
		else if (!mmxAddresses.isEmpty())
			return getRegAddress().getFullName();
		return stackAddresses.get(0).getFullName(offset);
	}

	public Address getRegAddress() {
		System.out.println("lffuzfuz" + this.type);
		if(this.type.equals("double*") || this.type.equals("double"))
			return mmxAddresses.get(0);
		return regAddresses.get(0);
	}

	public boolean onlyInReg() {
		return stackAddresses.size() == 0;
	}

	public boolean onStack() {
		return !stackAddresses.isEmpty();
	}

	public boolean inReg(int i) {
		for (RegisterAddress r : regAddresses)
			if (r.regNumber == i)
				return true;
		return false;
	}
	
	public boolean inMMXReg(int i) {
		for (MMXRegisterAddress r : mmxAddresses)
			if (r.regNumber == i)
				return true;
		return false;
	}
	
	public boolean inMMXReg() {
		return !mmxAddresses.isEmpty();
	}
}
