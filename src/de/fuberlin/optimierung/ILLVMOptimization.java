package de.fuberlin.optimization.ILLVMOptimization;

interface ILLVMOptimization {
	
	public String optimizeCodeFromString(String code);
	public String optimizeCodeFromFile(File file);
}
