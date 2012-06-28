package de.fuberlin.optimierung;

public interface ILLVM_Optimization {
	
	public String optimizeCodeFromString(String code);
	public String optimizeCodeFromFile(String fileName);
	public String getCode();
}
