package de.fuberlin.optimierung;

import java.io.*;

class LLVMOptimization implements ILLVMOptimization {
	
	private String code = "";

	private ILLVMBlock startBlock;
	private ILLVMBlock endBlock;
	private ILLVMBlock blocks[];
	private int numberBlocks;
	
	private LLVMRegisterMap registerMap = new LLVMRegisterMap();
	
	private void createRegisterMaps() {
		
		for(ILLVMBlock block : this.blocks) {	// Gehe Bloecke durch
			
			for(ILLVMCommand c = block.getFirstCommand(); !c.isLastCommand(); c = c.getSuccessor()) {
				
				// Fuege c in Register Maps ein
				this.registerMap.addCommand(c);
			}
	
		}
	}

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
		
		// Rekursiv durch den Block-Graph durch ausgeben
		return startBlock.toString();
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
		String optimizedCode = optimization.optimizeCodeFromFile("bin/de/fuberlin/optimierung/input/llvm_test.llvm");
		System.out.println(optimizedCode);
	}

}
