package de.fuberlin.projectF.CodeGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fuberlin.projectF.CodeGenerator.model.Array;
import de.fuberlin.projectF.CodeGenerator.model.ArrayPointer;
import de.fuberlin.projectF.CodeGenerator.model.MMXRegisterAddress;
import de.fuberlin.projectF.CodeGenerator.model.Record;
import de.fuberlin.projectF.CodeGenerator.model.RecordPointer;
import de.fuberlin.projectF.CodeGenerator.model.Reference;
import de.fuberlin.projectF.CodeGenerator.model.RegisterAddress;
import de.fuberlin.projectF.CodeGenerator.model.StackAddress;
import de.fuberlin.projectF.CodeGenerator.model.Variable;

public class MemoryContext {

	private String name;
	private int stackPointer;
	private HashMap<String, Reference> references;
	private HashMap<String, Variable> variables;
	private HashMap<String, Array> arrays;
	private HashMap<String, Record> records;
	private HashMap<String, ArrayPointer> arrayPtrs;
	private HashMap<String, RecordPointer> recordPtrs;
	private ArrayList<RegisterAddress> freeRegisters;
	private HashMap<RegisterAddress, Reference> usedRegisters;
	private ArrayList<MMXRegisterAddress> freeMMXRegisters;
	private HashMap<MMXRegisterAddress, Variable> usedMMXRegisters;

	public MemoryContext(String name) {
		this.name = name;
		stackPointer = 0;
		references = new HashMap<String, Reference>();
		variables = new HashMap<String, Variable>();
		arrays = new HashMap<String, Array>();
		records = new HashMap<String, Record>();
		arrayPtrs = new HashMap<String, ArrayPointer>();
		recordPtrs = new HashMap<String, RecordPointer>();
		freeRegisters = new ArrayList<RegisterAddress>();
		usedRegisters = new HashMap<RegisterAddress, Reference>();
		freeMMXRegisters = new ArrayList<MMXRegisterAddress>();
		usedMMXRegisters = new HashMap<MMXRegisterAddress, Variable>();
		for (int i = 0; i < 6; i++) {
			freeRegisters.add(new RegisterAddress(i));
		}
		for (int i = 0; i < 8; i++) {
			freeMMXRegisters.add(new MMXRegisterAddress(i));
		}

	}
	
	public String getName() {
		return name;
	}

	public void addMMXRegVar(String name, String type, MMXRegisterAddress reg) {
		Variable var = new Variable(name, type, reg);
		put(var);
		freeMMXRegisters.remove(reg);
		usedMMXRegisters.put(reg, var);
	}

	// Vorhandene Registervariable hinzufügen, z. B. um Rückgabewert zu
	// speichern
	public void addRegVar(String name, String type, RegisterAddress reg) {
		Variable var = new Variable(name, type, reg);
		put(var);
		freeRegisters.remove(reg);
		usedRegisters.put(reg, var);
	}

	// Vorhandene Stackvariable hinzufügen, z. B. Funktionsparameter, nach
	// Aufruf
	public void addStackVar(String name, String type, int stackAddress) {
		Variable newVar = new Variable(name, type, stackAddress);
		put(newVar);
	}

	public ArrayPointer contArrayPtr(String name, String lastPtr, String offset,
			String values) {
		System.out.println(values);
		int value = 1;
		values = values.substring(values.indexOf('x'), values.lastIndexOf('x'));
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(values);
		System.out.println(values);

		while (m.find()){
			value *= new Integer(m.group(0));
		}
		
		System.out.println(value);

		
		ArrayPointer oldPtr = arrayPtrs.get(lastPtr);
		ArrayPointer arrPtr = new ArrayPointer(name, oldPtr, value);
		put(arrPtr);
		return arrPtr;
	}

	public void freeMMXRegister(MMXRegisterAddress tmp) {
		usedMMXRegisters.remove(tmp.regNumber);
		freeMMXRegisters.add(0, tmp);
	}

	public void freeRegister(RegisterAddress reg) {
		usedRegisters.remove(reg);
		freeRegisters.add(reg);
	}

	public Reference get(String name) {
		return references.get(name);
	}

	public MMXRegisterAddress getFreeMMXRegister() {
		try {
			return freeMMXRegisters.get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public MMXRegisterAddress getFreeMMXRegister(int i) {
		for (MMXRegisterAddress r : freeMMXRegisters)
			if (r.regNumber == i)
				return r;
		return null;
	}

	public RegisterAddress getFreeRegister() {
		try {
			return freeRegisters.get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public RegisterAddress getFreeRegister(int i) {
		for (RegisterAddress r : freeRegisters)
			if (r.regNumber == i)
				return r;
		return null;
	}

	public List<Variable> getMMXRegVariables(boolean exclusive) {
		ArrayList<Variable> vars = new ArrayList<Variable>(
				usedMMXRegisters.values());
		for (Variable v : vars) {
			if (v.onStack())
				vars.remove(v);
		}
		return vars;
	}

	public List<Variable> getRegVariables(boolean exclusive) {
		ArrayList<Variable> vars = new ArrayList<Variable>(variables.values());
		for (Variable v : variables.values()) {
			if (v.onStack() || v.inMMXReg())
				vars.remove(v);
		}
		return vars;
	}

	public Variable getVarFromMMXReg(int regNumber) {
		// return registerInfo[regNumber];
		for (MMXRegisterAddress reg : usedMMXRegisters.keySet()) {
			if (reg.regNumber == regNumber)
				return usedMMXRegisters.get(reg);
		}
		return null;
	}

	public Reference getVarFromReg(int regNumber) {
		// return registerInfo[regNumber];
		for (RegisterAddress reg : usedRegisters.keySet()) {
			if (reg.regNumber == regNumber)
				return usedRegisters.get(reg);
		}
		return null;
	}

	public boolean inMMXReg(String name) {
		if (variables.containsKey(name))
			return variables.get(name).inMMXReg();
		return false;
	}

	public boolean inMMXReg(String name, int regNumber) {
		if (variables.containsKey(name))
			return variables.get(name).inMMXReg(regNumber);
		return false;
	}

	public boolean inReg(String name, int regNumber) {
		if (variables.containsKey(name))
			return variables.get(name).inReg(regNumber);
		return false;
	}

	public boolean MMXRegisterInUse(int i) {
		return usedMMXRegisters.containsKey(i);
	}

	public void MMXRegToStack(Variable var) {
		stackPointer -= var.getSize();
		MMXRegisterAddress reg = var.getMMXRegAddress();
		freeMMXRegisters.add(reg);
		usedMMXRegisters.remove(reg);
		var.addStackAddress(new StackAddress(stackPointer));
	}

	public Array newArray(String name, String type, int length) {
		Array newArr = new Array(name, type, length, stackPointer);

		stackPointer -= newArr.getSize();
		put(newArr);
		return newArr;
	}
	
	public Record newRecord(Record rec) {
		rec.setAddress(stackPointer);
		put(rec);
		return rec;
	}

	public ArrayPointer newArrayPtr(String name, String arrayName, String values, RegisterAddress reg) {
		Array arr = arrays.get(arrayName);
		int value = arr.getLength() / new Integer(values.split(" ")[1]);
		
		ArrayPointer arrPtr = new ArrayPointer(name, arr, value, reg);
		stackPointer -= arrPtr.getSize();
		put(arrPtr);
		
		freeRegisters.remove(reg);
		usedRegisters.put(reg, arrPtr);
		return arrPtr;
	}

	public RecordPointer newRecordPtr(String name, String rec, String offset) {
		System.out.println("Name: " + name);
		System.out.println("Record: " + rec);
		if(isRecordPtr(rec))
			System.out.println("PointerAddress " + recordPtrs.get(rec).getAddress(Integer.valueOf(offset)));
		else
			System.out.println("Address " + records.get(rec).getAddress(Integer.valueOf(offset)));
		System.out.println("Offset: " + offset);
		//TODO cast
		
		RecordPointer tmp;
		if(isRecordPtr(rec))
			tmp = new RecordPointer(name, recordPtrs.get(rec),new Integer(offset));
		else
			tmp = new RecordPointer(name, records.get(rec),new Integer(offset));
		put(tmp);
		return tmp;
	}

	public void newReference(String name, String var) {
		references.put(name, references.get(var));
	}

	// Creates new variable on stack
	public Variable newStackVar(String name, String type) {
		Variable newVar = new Variable(name, type, stackPointer);

		stackPointer -= newVar.getSize();

		put(newVar);
		return newVar;
	}

	public Variable newStackVar(Variable var) {
		int size = var.getSize();

		stackPointer -= size;
		System.out.println("new Stack var " + var.getName());
		System.out.println("Stackpointer: " + stackPointer);
		var.addStackAddress(new StackAddress(stackPointer));
		System.out.println("Address " + var.getAddress());
		put(var);
		System.out.println("Address "
				+ variables.get(var.getName()).getAddress());
		return var;
	}

	public boolean onStack(String name) {
		//if (variables.containsKey(name))
			//return variables.get(name).onStack();
		if (references.containsKey(name)) return references.get(name).onStack();
		return false;
	}

	private void put(Array arr) {
		String name = arr.getName();
		arrays.put(name, arr);
		references.put(name, arr);
	}
	
	private void put(Record rec) {
		String name = rec.getName();
		records.put(name, rec);
		references.put(name, rec);
	}

	private void put(ArrayPointer arrPtr) {
		String name = arrPtr.getName();
		arrayPtrs.put(name, arrPtr);
		references.put(name, arrPtr);
	}
	
	private void put(RecordPointer recPtr) {
		String name = recPtr.getName();
		recordPtrs.put(name, recPtr);
		references.put(name, recPtr);
	}

	private void put(Variable var) {
		String name = var.getName();
		variables.put(name, var);
		references.put(name, var);
	}

	public boolean registerInUse(int i) {
		return usedRegisters.containsKey(i);
	}

	public StackAddress regToStack(Variable var) {
		stackPointer -= var.getSize();
		RegisterAddress reg = var.getRegAddress();
		StackAddress movedTo = new StackAddress(stackPointer);
		var.addStackAddress(movedTo);
		var.freeRegister(reg);
		freeRegister(reg);
		return movedTo;
	}

	public Array getArray(String name) {
		return arrays.get(name);
	}

	public boolean isArrayPtr(String name) {
		return arrayPtrs.containsKey(name);
	}

	public ArrayPointer getArraPtr(String name) {
		return arrayPtrs.get(name);
	}
	
	public boolean isRecordPtr(String name) {
		return recordPtrs.containsKey(name);
	}

	public RecordPointer getRecordPtr(String name) {
		return recordPtrs.get(name);
	}

	public HashMap<RegisterAddress, Reference> getUsedRegisters() {
		return usedRegisters;
	}
}
