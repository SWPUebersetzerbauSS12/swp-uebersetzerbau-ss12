package de.fuberlin.projectF.CodeGenerator.model;

public class Array extends Reference {
	StackAddress address;
	int typeSize;

	public Array(String name, String type, int length, int stackAddress) {
		super(name, type);
		this.typeSize = this.size;
		size *= length;
		address = new StackAddress(stackAddress - size);
	}
	
	public int getLength(){
		return size / typeSize;
	}
	
	public int getTypeSize(){
		return typeSize;
	}

	@Override
	public String getAddress() {
		return address.getFullName();
	}
	
	public String getAddress(int offset) {
		return address.getFullName(-offset * typeSize);
	}
	
	@Override
	public boolean onStack(){
		return true;
	}
}
