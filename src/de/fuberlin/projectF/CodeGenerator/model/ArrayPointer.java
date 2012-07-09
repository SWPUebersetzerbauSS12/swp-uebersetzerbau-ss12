package de.fuberlin.projectF.CodeGenerator.model;

public class ArrayPointer extends Variable{
	private Array arr;
	private int offset;
	
	public ArrayPointer(Variable variable, int offset)
	{
		this.arr = (Array) variable;
		this.offset = offset;
	}
	@Override
	public String getAddress() {
		System.out.println("Xxx " + arr.stackAddresses.get(0).getAddress());
		return arr.getAddress(-offset * arr.getTypeSize());
	}
}
