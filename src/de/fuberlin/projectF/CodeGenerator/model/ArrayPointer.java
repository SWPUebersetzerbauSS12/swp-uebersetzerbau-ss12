package de.fuberlin.projectF.CodeGenerator.model;

public class ArrayPointer extends Reference{
	Array array;
	int value;
	RegisterAddress address;
	
	public ArrayPointer(String name, Array arr, int value, RegisterAddress address) {
		super(name, arr.getType(), 4);
		this.address = address;
		this.array = arr;
		this.value = value;
	}
	
	public ArrayPointer(String name, ArrayPointer lastPtr, int value) {
		super(name, lastPtr.type, 4);
		this.address = lastPtr.address;
		this.array = lastPtr.array;
		this.value = value;
	}

	@Override
	public String getAddress() {
		return " " + address.getFullName();
	}
	
	public String getPtrAddress() {
		return address.getFullName();
	}
	
	public Array getArray(){
		return this.array;
	}
	
	public int getValue(){
		return this.value;
	}

	@Override
	public String getAddress(int offset) {
		return " " + address.getFullName();
	}
	
	public boolean onStack(){
		return array.onStack();
	}
}
