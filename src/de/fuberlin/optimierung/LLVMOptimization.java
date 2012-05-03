package de.fuberlin.optimierung;

import java.io.*;

class LLVMOptimization implements ILLVMOptimization {
	
	private String code = "";

	private ILLVMBlock startBlock;
	private ILLVMBlock endBlock;
	private ILLVMBlock blocks[];
	private int numberBlocks;

	private void parseCode() {
		
		// Splitte Codestring in Bloecke
		String codeBlocks[] = this.code.split("\n\n");
		this.numberBlocks = codeBlocks.length-1;
		this.blocks = new LLVMBlock[this.numberBlocks];
		for(int i=1; i<=this.numberBlocks; i++) {
			this.blocks[i-1] = new LLVMBlock(codeBlocks[i]);
		}
		this.startBlock = this.blocks[0];
		this.endBlock = this.blocks[this.numberBlocks-1];

	}

	private String optimizeCode() {
		// Code steht als String in this.code
		// Starte Optimierung
		this.parseCode();

		return "";
	}

	private void readCodeFromFile(String fileName){
		
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
			String line = "";
			while((line = fileReader.readLine()) != null) {
				this.code = this.code + line;
				this.code = this.code + "\n";
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String optimizeCodeFromString(String code) {

		this.code = code;
		return this.optimizeCode();

	}

	public String optimizeCodeFromFile(String fileName) {

		this.readCodeFromFile(fileName);
		return this.optimizeCode();

	}
	
	public static void main(String args[]) {

		ILLVMOptimization optimization = new LLVMOptimization();
		optimization.optimizeCodeFromFile("../input/llvm_test");

	}

}
