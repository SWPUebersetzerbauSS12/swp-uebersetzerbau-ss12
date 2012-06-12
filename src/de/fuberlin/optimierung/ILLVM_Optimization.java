package de.fuberlin.optimierung;

interface ILLVM_Optimization {
	
	public String optimizeCodeFromString(String code);
	public String optimizeCodeFromFile(String fileName);
}
