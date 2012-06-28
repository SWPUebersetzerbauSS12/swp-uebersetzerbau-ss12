package de.fuberlin.projectF.CodeGenerator.model;

import java.util.HashMap;
import java.util.Map.Entry;

public class Record extends Variable implements Cloneable{
HashMap<String, Variable> variableList;

	public Record(String name) {
		this.name = name;
		variableList = new HashMap<String,Variable>();
	}
	
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
	public void add(Variable variable) {
		variableList.put(variable.name, variable);
		computeSize();
	}
	
	public void remove(Variable variable) {
		variableList.remove(variable.name);
		computeSize();
	}
	
	public Variable get(String name) {
		return variableList.get(name);
	}
	
	public int getVariableCount() {
		return variableList.size();
	}
	
	public void computeSize() {
		int size = 0;
		for(Entry<String, Variable> v : variableList.entrySet()) {
			size += v.getValue().getSize();
		}
		this.size = size;
	}
}
