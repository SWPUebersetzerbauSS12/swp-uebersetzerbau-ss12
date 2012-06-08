package main;

import java.util.HashMap;
import java.util.List;

import main.model.RegisterAddress;
import main.model.Variable;

public class MemoryManager {
	HashMap<String, MemoryContext> contexts;
	HashMap<String, Variable> heap;
	MemoryContext current;

	public MemoryManager() {
		contexts = new HashMap<String, MemoryContext>();
		heap = new HashMap<String, Variable>();
	}

	public void newContext(String name) {
		contexts.put(name, new MemoryContext());
	}

	public void setContext(String name) {
		current = contexts.get(name);
	}

	public void addHeapVar(String name, int size) {
		Variable tmp = new Variable("ascii", size, name);
		heap.put(name, tmp);
	}
	
	public Variable getHeapVar(String name) {
		return heap.get(name);
	}
	
	public Variable newStackVar(String name, String type) {
		return current.newStackVar(name, type);
	}
	
	public void regToStack(Variable var)
	{
		current.regToStack(var);
	}

	public String getAddress(String name) {
		System.out.println(current.get(name));
		return current.get(name).getAddress();
	}

	public void newVirtualVar(String name, String var) {
		current.newVirtualVar(name, var);
	}

	public RegisterAddress getFreeRegister() {
		return current.getFreeRegister();
	}

	public void addRegVar(String name, String type, RegisterAddress reg) {
		current.addRegVar(name, type, reg);

	}

	public void addStackVar(String name, String type, int stackAddress) {
		current.addStackVar(name, type, stackAddress);
	}

	public boolean onStack(String name) {
		if (heap.containsKey(name)) return false;
		return current.onStack(name);
	}

	public void freeRegister(RegisterAddress tmp) {
		current.freeRegister(tmp);
	}
	
	public Variable getVarFromReg(int regNumber) {
		return current.getVarFromReg(regNumber);
	}
	
	public boolean registerInUse(int i) {
		return current.registerInUse(i);
	}
	
	public List<Variable> getRegVariables(boolean exclusive){
		return current.getRegVariables(exclusive);
	}

	public boolean inReg(String name, int regNumber) {
		if (heap.containsKey(name)) return false;
		return current.inReg(name, regNumber);
	}

	public RegisterAddress getFreeRegister(int i) {
		return current.getFreeRegister(i);
	}
}
