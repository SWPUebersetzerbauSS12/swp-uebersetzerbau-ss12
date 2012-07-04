package de.fuberlin.projectF.CodeGenerator.model;

public abstract class Reference {
	// Properties of refered object
	String name, type;
	int size;
	
	public Reference (String name, String type) {
		this.name = name;
		this.type = type;
		
		if(this.type.equals("double")) this.size = 8;
		else this.size =  4;	
	}

	public Reference(String name, String type, int size) {
		this.name = name;
		this.type = type;
		this.size = size;
	}

	public abstract String getAddress();
	public abstract String getAddress(int offset);
		
	
	public int getSize() {
		return this.size;
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}

	public boolean onStack() {
		return false;
	}
}
