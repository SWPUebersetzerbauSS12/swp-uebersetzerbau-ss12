package de.fuberlin.projectF.CodeGenerator.model;

public class ArrayPointer extends Variable{
	private Array arr;
	private int offset;
	
	public ArrayPointer(Variable variable, int offset)
	{
		this.arr = (Array) variable;
		this.offset = offset;
	}
	
	public ArrayPointer(ArrayPointer ptr, int offset)
	{
		this.arr = ptr.getArray();
		this.offset = (offset + 1) * (ptr.getOffset() + 1) - 1;
	}
	
	@Override
	public String getAddress() {
		System.out.println("Xxx " + arr.stackAddresses.get(0).getAddress());
		return arr.getAddress(-offset * arr.getTypeSize());
	}
	
	public Array getArray(){
		return this.arr;
	}
	
	public int getOffset(){
		return this.offset;
	}
}
