package de.fuberlin.projectF.CodeGenerator.model;

public class RecordPointer extends Variable{
	private Record rec;
	private int offset;
	
	public RecordPointer(Variable variable, int offset)
	{
		super("undefined", "recordPointer");
		this.rec = (Record) variable;
		this.offset = offset;
	}
	@Override
	public String getAddress() {
		System.out.println("Xxx " + rec.stackAddresses.get(0).getAddress() + " offset " + offset);
		int addr = 0;
		
		for( int i = 0; i < offset; i++) {
			addr += rec.get(String.valueOf(i)).getSize();
		}	
		
		return rec.getAddress(addr);
	}
}
