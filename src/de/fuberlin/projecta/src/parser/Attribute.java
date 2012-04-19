package parser;

public class Attribute {
	private String name, value;
	
	public Attribute(String name){
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getName(){
		return name;
	}
}
