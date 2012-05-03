package de.fuberlin.optimierung;

import java.io.*;

class LLVMOptimization implements ILLVMOptimization {
	
	private String code = "";

	private ILLVMBlock startBlock;
	private ILLVMBlock endBlock;
	private ILLVMBlock blocks[];

	private void parseCode() {
		// parse
	}

	private void readCodeFromFile(String fileName){
		
		try {// IO Read File
		BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
		String line = "";
		while((line = fileReader.readLine()) != null) {
			this.code = this.code + line;
			this.code = this.code + "\n";
		}
		
			fileReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String optimizeCodeFromString(String code) {

		this.code = code;
		// TODO
		return "";

	}

	public String optimizeCodeFromFile(String fileName) {

		this.readCodeFromFile(fileName);
		// TODO
		return "";

	}
	
	public static void main(String args[]) {

		LLVMOptimization optimization = new LLVMOptimization();
		optimization.readCodeFromFile("optllvm_test");

	}

}
