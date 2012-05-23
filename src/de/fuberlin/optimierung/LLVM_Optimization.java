package de.fuberlin.optimierung;

import java.io.*;
import java.util.LinkedList;

class LLVM_Optimization implements ILLVM_Optimization {
	
	private String code = "";
	
	private LinkedList<LLVM_Function> functions;
	
	
	public LLVM_Optimization(){
		functions = new LinkedList<LLVM_Function>();
	}
	
	private void parseCode() {
		
		// Splitte in Funktionen
		String[] functions = this.code.split("define");
		
		for (int i = 1; i < functions.length; i++) {
			this.functions.add(new LLVM_Function(functions[i]));
		}
	}

	private String optimizeCode() {
		// Code steht als String in this.code
		// Starte Optimierung
		this.parseCode();
		
		String outputLLVM = "";
		LLVM_Function tmp;
		
		for (int i = 0; i < functions.size(); i++) {
			// aktuelle Funktion fuer Optimierung
			tmp = functions.get(i);
			
			// Erstelle Flussgraph
			tmp.createFlowGraph();
			
			
			
			// Optimierungsfunktionen
			tmp.createRegisterMaps();
			
			//Constant Folding
			tmp.constantFolding();
			
			// Dead register elimination
			tmp.eliminateDeadRegisters();
			//tmp.eliminateDeadBlocks();
			
			
			
			// Optimierte Ausgabe
			outputLLVM += tmp.toString();
		}
		
		return outputLLVM;
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

		ILLVM_Optimization optimization = new LLVM_Optimization();        
		String optimizedCode = optimization.optimizeCodeFromFile("input/llvm_constant_folding1");
		System.out.println(optimizedCode);
	}

}
