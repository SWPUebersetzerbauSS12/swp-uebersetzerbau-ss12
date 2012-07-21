package de.fuberlin.projectF.CodeGenerator;

import java.util.HashMap;
import java.util.List;

import de.fuberlin.projectF.CodeGenerator.model.Array;
import de.fuberlin.projectF.CodeGenerator.model.ArrayPointer;
import de.fuberlin.projectF.CodeGenerator.model.MMXRegisterAddress;
import de.fuberlin.projectF.CodeGenerator.model.Record;
import de.fuberlin.projectF.CodeGenerator.model.Reference;
import de.fuberlin.projectF.CodeGenerator.model.RegisterAddress;
import de.fuberlin.projectF.CodeGenerator.model.StackAddress;
import de.fuberlin.projectF.CodeGenerator.model.Variable;

public class MemoryManager {
	HashMap<String, MemoryContext> contexts;
	HashMap<String, Variable> globalReferences;
	MemoryContext current;

	public MemoryManager() {
		contexts = new HashMap<String, MemoryContext>();
		globalReferences = new HashMap<String, Variable>();
	}

	public void addGlobalVar(String name) {
		Variable tmp;
		if (current != null) {
			tmp = new Variable(current.getName() + "." + name, 0);
		} else {
			tmp = new Variable("main." + name, 0);
		}
		globalReferences.put(tmp.getName(), tmp);
	}

	public void addGlobalVar(Variable var) {
		globalReferences.put(var.getName(), var);
	}

	public String getContextName() {
		if (current != null)
			return current.getName();
		return "main";
	}

	public void addMMXRegVar(String name, String type, MMXRegisterAddress reg) {
		current.addMMXRegVar(name, type, reg);
	}

	public void addRegVar(String name, String type, RegisterAddress reg) {
		current.addRegVar(name, type, reg);
	}

	public void addStackVar(String name, String type, int stackAddress) {
		current.addStackVar(name, type, stackAddress);
	}

	public ArrayPointer contArrayPtr(String name, String lastPtr,
			String offset, String value) {
		return current.contArrayPtr(name, lastPtr, offset, value);
	}

	public void freeMMXRegister(MMXRegisterAddress tmp) {
		current.freeMMXRegister(tmp);
	}

	public void freeRegister(RegisterAddress k) {
		current.freeRegister(k);
	}

	public String getAddress(String name) {
		if (current != null) {
			if (globalReferences.containsKey(current.getName() + "." + name))
				return globalReferences.get(current.getName() + "." + name)
						.getName();
			return current.get(name).getAddress();
		} else {
			if (globalReferences.containsKey("main." + name))
				return globalReferences.get("main." + name).getName();
			return null;
		}
	}

	public String getAddress(String name, int offset) {
		return current.get(name).getAddress(offset);
	}

	public MMXRegisterAddress getFreeMMXRegister() {
		return current.getFreeMMXRegister();
	}

	public RegisterAddress getFreeRegister() {
		return current.getFreeRegister();
	}

	public List<Variable> getMMXRegVariables(boolean exclusive) {
		return current.getMMXRegVariables(exclusive);
	}

	public List<Variable> getRegVariables(boolean exclusive) {
		return current.getRegVariables(exclusive);
	}

	public boolean inMMXReg(String name) {
		if (globalReferences.containsKey(name))
			return false;
		return current.inMMXReg(name);
	}

	public boolean inReg(String name, int regNumber) {
		if (globalReferences.containsKey(name))
			return false;
		return current.inReg(name, regNumber);
	}

	public void MMXRegToStack(Variable var) {
		current.MMXRegToStack(var);
	}

	public Array newArray(String name, String type, int length) {
		return current.newArray(name, type, length);
	}

	public Record newRecord(Record rec) {
		return current.newRecord(rec);
	}

	public ArrayPointer newArrayPtr(String name, String arr, String offset,
			RegisterAddress reg) {
		return current.newArrayPtr(name, arr, offset, reg);
	}

	public void newContext(String name) {
		contexts.put(name, new MemoryContext(name));
	}

	public void newRecordPtr(String target, String op1, String op2) {
		current.newRecordPtr(target, op1, op2);
	}

	public void newReference(String name, String var) {
		if (globalReferences.containsKey(current.getName() + "." + var))
			globalReferences.put(current.getName() + "." + name,
					globalReferences.get(current.getName() + "." + var));
		else
			current.newReference(name, var);
	}

	public Variable newStackVar(String name, String type) {
		return current.newStackVar(name, type);
	}

	public boolean onStack(String name) {
		if (globalReferences.containsKey(name))
			return false;
		return current.onStack(name);
	}

	public StackAddress regToStack(Variable var) {
		return current.regToStack(var);
	}

	public void setContext(String name) {
		current = contexts.get(name);
	}

	public Array getArray(String name) {
		return current.getArray(name);
	}

	public HashMap<RegisterAddress, Reference> getUsedRegisters() {
		return current.getUsedRegisters();
	}

	public RegisterAddress getRegister(int i) {
		return current.getRegister(i);
	}

	public MMXRegisterAddress getMMXRegister(int i) {
		return current.getMMXRegister(i);
	}

	public boolean isFree(int i) {
		return current.isFree(i);
	}

	public HashMap<MMXRegisterAddress, Variable> getUsedMMXRegisters() {
		return current.getUsedMMXRegisters();
	}

	public StackAddress mmxRegToStack(Variable var) {
		return current.mmxRegToStack(var);
	}
}