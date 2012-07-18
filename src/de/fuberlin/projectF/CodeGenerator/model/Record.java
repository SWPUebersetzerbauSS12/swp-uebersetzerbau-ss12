package de.fuberlin.projectF.CodeGenerator.model;

import java.util.HashMap;
import java.util.Map.Entry;

public class Record extends Reference {
	HashMap<String, Reference> variableList;
	StackAddress address;

	public Record(String name) {
		super(name, "record");
		variableList = new HashMap<String,Reference>();
	}
	
	public void add(Reference reference) {
		variableList.put(reference.name, reference);
		computeSize();
	}
	
	public void remove(Reference reference) {
		variableList.remove(reference.name);
		computeSize();
	}
	
	public Reference get(String name) {
		return variableList.get(name);
	}
	
	public int getVariableCount() {
		return variableList.size();
	}
	
	public void computeSize() {
		int size = 0;
		for(Entry<String, Reference> v : variableList.entrySet())
			size += v.getValue().getSize();
		this.size = size;
	}

	@Override
	public String getAddress() {
		return address.getFullName();
	}

	@Override
	public String getAddress(int var) {
		return variableList.get(String.valueOf(var)).getAddress();
	}
	
	public String getAddress(int var, int offset) {
		return variableList.get(String.valueOf(var)).getAddress(offset);
	}
	
	public void setAddress(int stackAddress) {
		address = new StackAddress(stackAddress - size);
	}
}
