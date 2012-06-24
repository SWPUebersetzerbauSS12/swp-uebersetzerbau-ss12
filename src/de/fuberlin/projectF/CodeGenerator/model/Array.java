package de.fuberlin.projectF.CodeGenerator.model;

public class Array extends Variable {
	int typeSize;
	
	public Array(String type, int size, int typeSize, int stackAddress, String name) {
		super(type, size, stackAddress, name);
		this.typeSize = typeSize;
	}

	public int getTypeSize() {
		return typeSize;
	}

}
