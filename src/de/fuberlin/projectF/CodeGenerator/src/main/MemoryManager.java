package main;

import java.util.HashMap;

import main.model.RegisterAddress;
import main.model.Variable;

public class MemoryManager {
	HashMap<String, MemoryContext> contexts;
	MemoryContext current;

	public MemoryManager() {
		contexts = new HashMap<String, MemoryContext>();
	}

	public void newContext(String name) {
		contexts.put(name, new MemoryContext());
	}

	public void setContext(String name) {
		current = contexts.get(name);
	}

	public Variable newStackVar(String name, String type) {
		return current.newStackVar(name, type);
	}

	public String getAddress(String name) {
		return current.get(name).getAddress();
	}

	public void newVirtualVar(String name, String var) {
		current.newVirtualVar(name, var);
	}

	public RegisterAddress getFreeRegister() {
		return current.getFreeRegister();
	}

	public void addRegVar(String name, String type, RegisterAddress sum) {
		current.addRegVar(name, type, sum);

	}

	public void addStackVar(String name, String type, int stackAddress) {
		current.addStackVar(name, type, stackAddress);
	}

	public void setReturnRegister(RegisterAddress ret) {
		current.setReturnRegister(ret);

	}

	public RegisterAddress getReturnRegister(String function) {
		return contexts.get(function).getReturnRegister();
	}

	public boolean onStack(String name) {
		if (current.containsKey(name))
			return current.get(name).getAddress().endsWith(")");
		else
			return false;
	}

	public void freeRegister(RegisterAddress tmp) {
		current.freeRegister(tmp);
	}
}
