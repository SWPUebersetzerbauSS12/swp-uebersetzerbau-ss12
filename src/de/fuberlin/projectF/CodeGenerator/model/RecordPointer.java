package de.fuberlin.projectF.CodeGenerator.model;

public class RecordPointer extends Variable{
	private Record arr;
	private int offset;
	
	public RecordPointer(Variable variable, int offset)
	{
		this.arr = (Record) variable;
		this.offset = offset;
	}
	@Override
	public String getAddress() {
		System.out.println("Xxx " + arr.stackAddresses.get(0).getAddress());
		return arr.getAddress(-offset * arr.getSize());
	}
}
