package de.fuberlin.optimierung;

public interface ILLVM_Optimization {
	
	/*
	 * Optimiert LLVM Code
	 * @param String code
	 * @ret String optimized_code
	 * @exception wirft LLVM_OptimizationException
	 */
	public String optimizeCodeFromString(String code) throws LLVM_OptimizationException;
	
	/*
	 * Optimiert LLVM Code aus Datei
	 * @param String filePath
	 * @ret String optimized_code
	 * @exception wirft LLVM_OptimizationException
	 */
	public String optimizeCodeFromFile(String fileName) throws LLVM_OptimizationException;
	
	/*
	 * Gibt den unoptimierten Code wieder
	 * @ret String unoptimized_code
	 */
	public String getCode();
}
