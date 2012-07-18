package de.fuberlin.projectF.CodeGenerator.model;

public class RecordPointer extends Reference{
	private Record rec;
	private RecordPointer pRec;
	private StackAddress addr;
	private int var;
	
	public RecordPointer(String name, Record record, int var)
	{
		super(name, "recordPointer");
		this.rec =  record;
		this.var = var;
	}
	public RecordPointer(String name, RecordPointer recordPointer , int var)
	{
		super(name, "recordPointer");
		this.pRec =  recordPointer;
		this.var = var;
	}

	public String getAddress() {
		System.out.println("Get Address of " + rec);
		if(rec != null)
			return rec.getAddress(var);
		return pRec.getAddress(var);
	}

	public String getAddress(int offset) {
		if(rec != null)
			return rec.getAddress(var, offset);
		return pRec.getAddress(offset);
	}
	
	public int getSize() {
		return 0;
	}
}
