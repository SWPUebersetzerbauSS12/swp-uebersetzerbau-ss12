package de.fuberlin.projectF.CodeGenerator.model;

public class RecordPointer extends Reference{
	private Record rec;
	private RecordPointer pRec;
	private StackAddress addr;
	private int offset;
	
	public RecordPointer(String name, Record record, int offset)
	{
		super(name, "recordPointer");
		this.rec =  record;
		this.offset = offset;
	}
	public RecordPointer(String name, RecordPointer recordPointer , int offset)
	{
		super(name, "recordPointer");
		this.pRec =  recordPointer;
		this.offset = offset;
	}

	public String getAddress() {
		int addr = 0;
		
		for( int i = 0; i < offset; i++) {
			addr += rec.get(String.valueOf(i)).getSize();
		}	
		
		return rec.getAddress(addr);
	}

	public String getAddress(int offset) {
		int addr = offset;
		
		for( int i = 0; i < this.offset; i++) {
			addr += rec.get(String.valueOf(i)).getSize();
		}	
		
		return rec.getAddress(addr);
	}
	
	public int getSize() {
		return 0;
	}
}
