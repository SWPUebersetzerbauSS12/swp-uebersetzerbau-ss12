package main.model;

public class Variable {
	
	String value;
	String type;
	String name;
	int size;

	public Variable() {}
	
	public Variable(String name, String type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
		if(this.type != null && this.type.equals("i32"))
			size = 4;
	}
	
	public String value() {
		return this.value;
	}
	
	public String name() {
		return this.name;
	}
	
	public String type() {
		return this.type;
	}
	
	public void value(String value) {
		this.value = value;
	}
	
	public void name(String name) {
		this.name = value;
	}
	
	public void type(String type) {
		this.type = type;
	}
	
	public int size() {
		return this.size;
	}
}
