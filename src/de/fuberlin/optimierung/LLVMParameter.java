package de.fuberlin.optimierung;

public class LLVMParameter {

	private String typeString;
	private LLVMParameterType type;
	private String name;
	
	public LLVMParameter(String name, LLVMParameterType type, String typeString) {
		this.typeString = typeString;
		this.type = type;
		this.name = name;
	}
	
}
