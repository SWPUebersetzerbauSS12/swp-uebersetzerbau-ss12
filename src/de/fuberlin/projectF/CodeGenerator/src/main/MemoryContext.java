package main;

import java.util.ArrayList;
import java.util.HashMap;

import main.model.Variable;
import main.model.RegisterAddress;

public class MemoryContext {

	int stackVars;
	int stackPointer;
	private HashMap<String, Variable> variables;
	private RegisterAddress returnRegister;
	ArrayList<RegisterAddress> registers;

	public MemoryContext() {
		int stackVars = 0;
		int stackPointer = 0;
		variables = new HashMap<String, Variable>();
		registers = new ArrayList<RegisterAddress>();
		for (int i = 0; i < 6; i++) {
			registers.add(new RegisterAddress(i));
		}
	}

	public boolean containsKey(String name) {
		return variables.containsKey(name);
	}

	public Variable get(String name) {
		return variables.get(name);
	}

	public Variable newStackVar(String name, String type) {
		int size;
		if (type.equals("i32"))
			size = 4;
		else
			size = 4;

		stackVars++;
		stackPointer -= size;
		Variable newVar = new Variable(type, size, stackPointer);
		variables.put(name, newVar);
		return newVar;
	}

	public void newVirtualVar(String name, String var) {
		variables.put(name, variables.get(var));
	}

	public void newRegVar(String name, String type, RegisterAddress reg) {
		variables.put(name, new Variable(type, reg));
		registers.remove(reg);
	}

	public void addStackVar(String name, String type, int stackAddress) {
		int size;
		if (type.equals("i32"))
			size = 4;
		else
			size = 4;

		Variable newVar = new Variable(type, size, stackAddress);
		variables.put(name, newVar);
	}

	public void setReturnRegister(RegisterAddress ret) {
		returnRegister = ret;
	}

	public RegisterAddress getReturnRegister() {
		return returnRegister;
	}

	public void addRegister(RegisterAddress reg) {
		// TODO Auto-generated method stub

	}

	public RegisterAddress getFreeRegister() {
		return registers.remove(0);
	}

	public void freeRegister(RegisterAddress tmp) {
		registers.add(0, tmp);
	}

}
