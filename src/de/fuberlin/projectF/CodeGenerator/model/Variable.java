package de.fuberlin.projectF.CodeGenerator.model;

import java.util.ArrayList;

public class Variable extends Reference {
	ArrayList<StackAddress> stackAddresses;
	ArrayList<RegisterAddress> regAddresses;
	ArrayList<MMXRegisterAddress> mmxAddresses;

	public Variable(String name, String type) {
		super(name, type);
		stackAddresses = new ArrayList<StackAddress>();
		regAddresses = new ArrayList<RegisterAddress>();
		mmxAddresses = new ArrayList<MMXRegisterAddress>();
	}

	public Variable(String name, String type, int stackPointer) {
		this(name, type);
		stackAddresses.add(new StackAddress(stackPointer - size));
	}

	public Variable(String name, String type, RegisterAddress reg) {
		this(name, type);
		regAddresses.add(reg);
	}

	public Variable(String name, String type, MMXRegisterAddress reg) {
		this(name, type);
		mmxAddresses.add(reg);
	}

	public Variable(String name, int size) {
		super(name, "ascii", size);
	}

	@Override
	public String getAddress() {
		if (!regAddresses.isEmpty())
			return getRegAddress().getFullName();
		else if (!mmxAddresses.isEmpty()) {
			return getMMXRegAddress().getFullName();
		}
		return stackAddresses.get(0).getFullName();
	}
	
	public void addStackAddress(StackAddress stackAddress) {
		stackAddresses.add(stackAddress);
	}

	public RegisterAddress getRegAddress() {
		return regAddresses.get(0);
	}
	
	public MMXRegisterAddress getMMXRegAddress() {
		return mmxAddresses.get(0);
	}

	public boolean onlyInReg() {
		return stackAddresses.size() == 0;
	}

	@Override
	public boolean onStack() {
		return !stackAddresses.isEmpty();
	}

	public boolean inReg(int i) {
		for (RegisterAddress r : regAddresses)
			if (r.regNumber == i)
				return true;
		return false;
	}
	
	public boolean inReg() {
		return !regAddresses.isEmpty();
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

	
	public String getAddress(int offset) {
		if (!regAddresses.isEmpty())
			return getRegAddress().getFullName();
		else if (!mmxAddresses.isEmpty())
			return getMMXRegAddress().getFullName();
		return stackAddresses.get(0).getFullName(offset);
	}

	public void freeRegister(RegisterAddress reg) {
		regAddresses.remove(reg);		
	}

	public void freeMMXRegister(MMXRegisterAddress reg) {
		mmxAddresses.remove(reg);
		
	}
}