package de.fuberlin.optimierung;

interface ILLVMOptimization {
	
	public String optimizeCodeFromString(String code);
	public String optimizeCodeFromFile(String fileName);
}
