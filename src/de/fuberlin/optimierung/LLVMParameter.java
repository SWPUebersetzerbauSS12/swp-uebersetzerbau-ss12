package de.fuberlin.optimierung;

public class LLVMParameter {

	private String typeString;		// Bsp: "i32"
	private LLVMParameterType type;		// Bsp: REGISTER
	private String name;	// Bsp: %i
	
	public LLVMParameter(String name, String typeString) {
		
		if(name.charAt(0) == '%')
			type = LLVMParameterType.REGISTER;
		else
			type = LLVMParameterType.INTEGER;
		this.typeString = typeString;
		this.name = name;
	}
	
	public LLVMParameter(String name, LLVMParameterType type, String typeString) {
		this.typeString = typeString;
		this.type = type;
		this.name = name;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public LLVMParameterType getType() {
		return type;
	}

	public void setType(LLVMParameterType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
