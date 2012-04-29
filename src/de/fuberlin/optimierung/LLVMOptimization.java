package de.fuberlin.optimization.ILLVMOptimization;

import de.fuberlin.optimization.ILLVMBlock;

class LLVMOptimization implements ILLVMOptimization {
	
	private ILLVMBlock startBlock;
	
	public LLVMOptimization(String code){
		parseLLVMCode(code);
	}
	public LLVMOptimization(File file){
		
		// IO Read File
		
		parseLLVMCode(code);
	}
	
	public String optimizeCodeFromString(String code){
		parseLLVMCode(code);
	}
	public String optimizeCodeFromFile(File file){
		
		// IO Read File
		
		parseLLVMCode(code);
	}
	
	private void parseLLVMCode(String code){
		// parse LLVM
	}
}