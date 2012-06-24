package de.fuberlin.projectF.CodeGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fuberlin.projectF.CodeGenerator.model.Array;
import de.fuberlin.projectF.CodeGenerator.model.ArrayPointer;
import de.fuberlin.projectF.CodeGenerator.model.MMXRegisterAddress;
import de.fuberlin.projectF.CodeGenerator.model.StackAddress;
import de.fuberlin.projectF.CodeGenerator.model.Variable;
import de.fuberlin.projectF.CodeGenerator.model.RegisterAddress;

public class MemoryContext {

	int stackVars;
	int stackPointer;
	private HashMap<String, Variable> variables;
	ArrayList<RegisterAddress> freeRegisters;
	HashMap<RegisterAddress, Variable> usedRegisters;
	ArrayList<MMXRegisterAddress> freeMMXRegisters;
	HashMap<MMXRegisterAddress, Variable> usedMMXRegisters;

	public MemoryContext() {
		stackVars = 0;
		stackPointer = 0;
		variables = new HashMap<String, Variable>();
		freeRegisters = new ArrayList<RegisterAddress>();
		usedRegisters = new HashMap<RegisterAddress, Variable>();
		freeMMXRegisters = new ArrayList<MMXRegisterAddress>();
		usedMMXRegisters = new HashMap<MMXRegisterAddress, Variable>();
		for (int i = 0; i < 6; i++) {
			freeRegisters.add(new RegisterAddress(i));
		}
		for (int i = 0; i < 8; i++) {
			freeMMXRegisters.add(new MMXRegisterAddress(i));
		}
	}

	public Variable get(String name) {
		return variables.get(name);
	}

	// Neue Variable auf dem Stack angeben
	public Variable newStackVar(String name, String type) {
		int size = getSize(type);

		stackVars++;
		stackPointer -= size;
		Variable newVar = new Variable(type, size, stackPointer, name);
		variables.put(name, newVar);
		return newVar;
	}
	
	public Array newArrayVar(String name, String type, int length) {
		int typeSize = getSize(type);
		int size = typeSize * length;
		
		stackVars++;
		stackPointer -= typeSize;
		Array newArr = new Array(type, size, typeSize, stackPointer, name);
		stackPointer -= size + typeSize;
		variables.put(name, newArr);
		return newArr;
	}

	// Verweis auf Variable speichern
	public void newVirtualVar(String name, String var) {
		variables.put(name, variables.get(var));
	}
	
	public void regToStack(Variable var){
		stackVars++;
		stackPointer -= var.getSize();
		RegisterAddress reg = (RegisterAddress)var.getRegAddress(); 
		freeRegisters.add(reg);
		usedRegisters.remove(reg);
		var.addStackAddress(new StackAddress(stackPointer));
	}
	
	public void MMXRegToStack(Variable var){
		stackVars++;
		stackPointer -= var.getSize();
		MMXRegisterAddress reg = (MMXRegisterAddress)var.getRegAddress(); 
		freeMMXRegisters.add(reg);
		usedMMXRegisters.remove(reg);
		var.addStackAddress(new StackAddress(stackPointer));
	}

	// Vorhandene Registervariable hinzufügen, z. B. um Rückgabewert zu speichern
	public void addRegVar(String name, String type, RegisterAddress reg) {
		Variable var = new Variable(type, reg, name);
		variables.put(name, var);
		freeRegisters.remove(reg);
		usedRegisters.put(reg, var);
	}
	
	public void addMMXRegVar(String name, String type, MMXRegisterAddress reg) {
		Variable var = new Variable(type, reg, name);
		variables.put(name, var);
		freeMMXRegisters.remove(reg);
		usedMMXRegisters.put(reg, var);
	}

	// Vorhandene Stackvariable hinzufügen, z. B. Funktionsparameter, nach Aufruf
	public void addStackVar(String name, String type, int stackAddress) {
		int size = getSize(type);

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
	
	public MMXRegisterAddress getFreeMMXRegister() {
		try {
			return freeMMXRegisters.get(0);
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
	
	public Variable getVarFromMMXReg(int regNumber) {
		//return registerInfo[regNumber];
		for (MMXRegisterAddress reg : usedMMXRegisters.keySet()) {
			if (reg.regNumber == regNumber) return usedMMXRegisters.get(reg);
		}
		return null;
	}
	
	public void freeRegister(RegisterAddress tmp) {
		usedRegisters.remove(tmp.regNumber);
		freeRegisters.add(0, tmp);
	}
	
	public void freeMMXRegister(MMXRegisterAddress tmp) {
		usedMMXRegisters.remove(tmp.regNumber);
		freeMMXRegisters.add(0, tmp);
	}
	
	public List<Variable> getRegVariables(boolean exclusive) {
		ArrayList<Variable> vars = new ArrayList<Variable>(usedRegisters.values());
		for (Variable v : vars) {
			if (v.onStack()) vars.remove(v);
		}
		return vars;
	}
	
	public List<Variable> getMMXRegVariables(boolean exclusive) {
		ArrayList<Variable> vars = new ArrayList<Variable>(usedMMXRegisters.values());
		for (Variable v : vars) {
			if (v.onStack()) vars.remove(v);
		}
		return vars;
	}
	
	public boolean registerInUse(int i) {
		return usedRegisters.containsKey(i);
	}
	
	public boolean MMXRegisterInUse(int i) {
		return usedMMXRegisters.containsKey(i);
	}

	public boolean onStack(String name) {
		if (!variables.containsKey(name)) return false;
		return variables.get(name).onStack();
	}

	public boolean inReg(String name, int regNumber) {
		if (!variables.containsKey(name)) return false;
		return variables.get(name).inReg(regNumber);
	}
	
	public boolean inMMXReg(String name, int regNumber) {
		if (!variables.containsKey(name)) return false;
		return variables.get(name).inMMXReg(regNumber);
	}
	
	public boolean inMMXReg(String name) {
		if (!variables.containsKey(name)) return false;
		return variables.get(name).inMMXReg();
	}

	public RegisterAddress getFreeRegister(int i) {
		for (RegisterAddress r : freeRegisters)
			if (r.regNumber == i) return r;
		return null;
	}
	
	public MMXRegisterAddress getFreeMMXRegister(int i) {
		for (MMXRegisterAddress r : freeMMXRegisters)
			if (r.regNumber == i) return r;
		return null;
	}
	
	public static int getSize(String type){
		if (type.equals("i32"))
			return 4;
		if(type.equals("double"))
			return 8;
		return 0;
	}

	public void newArrayPtr(String name, String arr, String offset) {
		variables.put(name, new ArrayPointer(variables.get(arr), new Integer(offset)));
	}

}
