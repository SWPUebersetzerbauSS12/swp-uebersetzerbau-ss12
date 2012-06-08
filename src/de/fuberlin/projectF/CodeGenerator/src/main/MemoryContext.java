package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.model.StackAddress;
import main.model.Variable;
import main.model.RegisterAddress;

public class MemoryContext {

	int stackVars;
	int stackPointer;
	private HashMap<String, Variable> variables;
	ArrayList<RegisterAddress> freeRegisters;
	HashMap<RegisterAddress, Variable> usedRegisters;
	//Variable[] registerInfo;

	public MemoryContext() {
		int stackVars = 0;
		int stackPointer = 0;
		variables = new HashMap<String, Variable>();
		freeRegisters = new ArrayList<RegisterAddress>();
		usedRegisters = new HashMap<RegisterAddress, Variable>();
		for (int i = 0; i < 6; i++) {
			freeRegisters.add(new RegisterAddress(i));
		}
	}

	public Variable get(String name) {
		return variables.get(name);
	}

	// Neue Variable auf dem Stack angeben
	public Variable newStackVar(String name, String type) {
		int size;
		if (type.equals("i32"))
			size = 4;
		else
			size = 4;

		stackVars++;
		stackPointer -= size;
		Variable newVar = new Variable(type, size, stackPointer, name);
		variables.put(name, newVar);
		return newVar;
	}

	// Verweis auf Variable speichern
	public void newVirtualVar(String name, String var) {
		variables.put(name, variables.get(var));
	}
	
	public void regToStack(Variable var){
		stackVars++;
		stackPointer -= var.getSize();
		RegisterAddress reg = var.getRegAddress(); 
		freeRegisters.add(reg);
		usedRegisters.remove(reg);
		var.addStackAddress(new StackAddress(stackPointer));
	}

	// Vorhandene Registervariable hinzufügen, z. B. um Rückgabewert zu speichern
	public void addRegVar(String name, String type, RegisterAddress reg) {
		Variable var = new Variable(type, reg, name);
		variables.put(name, var);
		freeRegisters.remove(reg);
		usedRegisters.put(reg, var);
	}

	// Vorhandene Stackvariable hinzufügen, z. B. Funktionsparameter, nach Aufruf
	public void addStackVar(String name, String type, int stackAddress) {
		int size;
		if (type.equals("i32"))
			size = 4;
		else
			size = 4;

		Variable newVar = new Variable(type, size, stackAddress, name);
		variables.put(name, newVar);
	}
	
	public void addStringVar(String name, int size, String type) {
		Variable newVar = new Variable(type, size, 0, name);
		variables.put(name, newVar);
	}

	public RegisterAddress getFreeRegister() {
		try {
			return freeRegisters.get(0);
		} catch(IndexOutOfBoundsException e) {
			return null;
		}
	}

	public Variable getVarFromReg(int regNumber) {
		//return registerInfo[regNumber];
		for (RegisterAddress reg : usedRegisters.keySet()) {
			if (reg.regNumber == regNumber) return usedRegisters.get(reg);
		}
		return null;
	}
	
	public void freeRegister(RegisterAddress tmp) {
		usedRegisters.remove(tmp.regNumber);
		freeRegisters.add(0, tmp);
	}
	
	public List<Variable> getRegVariables(boolean exclusive) {
		ArrayList<Variable> vars = new ArrayList<Variable>(usedRegisters.values());
		for (Variable v : vars) {
			if (v.onStack()) vars.remove(v);
		}
		return vars;
	}
	
	public boolean registerInUse(int i) {
		return usedRegisters.containsKey(i);
	}

	public boolean onStack(String name) {
		if (!variables.containsKey(name)) return false;
		return variables.get(name).onStack();
	}

	public boolean inReg(String name, int regNumber) {
		if (!variables.containsKey(name)) return false;
		return variables.get(name).inReg(regNumber);
	}

	public RegisterAddress getFreeRegister(int i) {
		for (RegisterAddress r : freeRegisters)
			if (r.regNumber == i) return r;
		return null;
	}

}
