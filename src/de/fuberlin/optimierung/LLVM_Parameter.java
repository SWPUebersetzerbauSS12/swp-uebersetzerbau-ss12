package de.fuberlin.optimierung;

public class LLVM_Parameter {

	private String typeString;		// Bsp: "i32"
	private LLVM_ParameterType type;		// Bsp: REGISTER
	private String name;	// Bsp: %i
	
	public LLVM_Parameter(String name, String typeString) {
		
		if (name.length() > 0){
			if (name.charAt(0) == '%')
				type = LLVM_ParameterType.REGISTER;
			else if(name.charAt(0) == '[')
				type = LLVM_ParameterType.ARRAY;
			else
				type = LLVM_ParameterType.INTEGER;
		}
		this.typeString = typeString;
		
		// Kommas entfernen
		this.typeString = typeString.replace(',', ' ').trim();
		this.name = name.replace(',', ' ').trim();
	}
	
	public LLVM_Parameter(String name, LLVM_ParameterType type, String typeString) {
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

	public LLVM_ParameterType getType() {
		return type;
	}

	public void setType(LLVM_ParameterType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
