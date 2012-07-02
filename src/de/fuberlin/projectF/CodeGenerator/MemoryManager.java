package de.fuberlin.projectF.CodeGenerator;

import java.util.HashMap;
import java.util.List;

import de.fuberlin.projectF.CodeGenerator.model.MMXRegisterAddress;
import de.fuberlin.projectF.CodeGenerator.model.RegisterAddress;
import de.fuberlin.projectF.CodeGenerator.model.Variable;

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
	
	public void addHeapVar(Variable var) {
		heap.put(var.name, var);
	}
	
	public boolean inHeap(String name) {
		if(!heap.containsKey(name)) return false;
		return true;
	}
	
	public Variable getHeapVar(String name) {
		return heap.get(name);
	}
	
	public Variable newStackVar(String name, String type) {
		return current.newStackVar(name, type);
	}
	
	public Variable newStackVar(Variable var) {
		return current.newStackVar(var);
	}
	
	public Variable newArrayVar(String name, String type, int length) {
		return current.newArrayVar(name, type, length);
	}
	
	public void regToStack(Variable var) {
		current.regToStack(var);
	}
	
	public void MMXRegToStack(Variable var) {
		current.MMXRegToStack(var);
	}

	public String getAddress(String name) {
		return current.get(name).getAddress();
	}
	
	public String getAddress(String name, int offset) {
		return current.get(name).getAddress(offset);
	}

	public void newVirtualVar(String name, String var) {
		current.newVirtualVar(name, var);
	}

	public RegisterAddress getFreeRegister() {
		return current.getFreeRegister();
	}
	
	public MMXRegisterAddress getFreeMMXRegister() {
		return current.getFreeMMXRegister();
	}

	public void addRegVar(String name, String type, RegisterAddress reg) {
		current.addRegVar(name, type, reg);
	}
	
	public void addMMXRegVar(String name, String type, MMXRegisterAddress reg) {
		current.addMMXRegVar(name, type, reg);
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
	
	public void freeMMXRegister(MMXRegisterAddress tmp) {
		current.freeMMXRegister(tmp);
	}
	
	public Variable getVarFromReg(int regNumber) {
		return current.getVarFromReg(regNumber);
	}
	
	public Variable getVarFromMMXReg(int regNumber) {
		return current.getVarFromMMXReg(regNumber);
	}
	
	public boolean registerInUse(int i) {
		return current.registerInUse(i);
	}
	
	public boolean MMXRegisterInUse(int i) {
		return current.MMXRegisterInUse(i);
	}
	
	public List<Variable> getRegVariables(boolean exclusive){
		return current.getRegVariables(exclusive);
	}
	
	public List<Variable> getMMXRegVariables(boolean exclusive){
		return current.getMMXRegVariables(exclusive);
	}

	public boolean inReg(String name, int regNumber) {
		if (heap.containsKey(name)) return false;
		return current.inReg(name, regNumber);
	}
	
	public boolean inMMXReg(String name, int regNumber) {
		if (heap.containsKey(name)) return false;
		return current.inMMXReg(name, regNumber);
	}
	
	public boolean inMMXReg(String name) {
		if (heap.containsKey(name)) return false;
		return current.inMMXReg(name);
	}

	public RegisterAddress getFreeRegister(int i) {
		return current.getFreeRegister(i);
	}
	
	public MMXRegisterAddress getFreeMMXRegister(int i) {
		return current.getFreeMMXRegister(i);
	}

	public void newArrayPtr(String name, String arr, String offset) {
		current.newArrayPtr(name, arr, offset);
	}

	public void newRecordPtr(String target, String op1, String op2) {
		current.newRecordPtr(target, op1, op2);	
	}
}
