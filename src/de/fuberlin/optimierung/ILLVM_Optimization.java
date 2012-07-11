package de.fuberlin.optimierung;

public interface ILLVM_Optimization {
	
	public String optimizeCodeFromString(String code) throws LLVM_OptimizationException;
	public String optimizeCodeFromFile(String fileName) throws LLVM_OptimizationException;
	public String getCode();
}
