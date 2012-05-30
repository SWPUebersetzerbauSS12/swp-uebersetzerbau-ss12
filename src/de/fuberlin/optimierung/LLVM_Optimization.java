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
			tmp.eliminateDeadBlocks();
			
			// Globale Lebendigkeitsanalyse fuer Store, Load
			tmp.globalLiveVariableAnalysis();
			tmp.eliminateDeadRegisters();
			
			// Optimierte Ausgabe
			outputLLVM += tmp.toString();
			
			createGraph("func"+i, tmp);
		}
		
		return outputLLVM;
	}

	private void createGraph(String filename, LLVM_Function func) {
		
		try{
			
			FileWriter fstream = new FileWriter(System.getProperty("user.home")+"/"+filename+".dot");
			
			BufferedWriter out = new BufferedWriter(fstream);
			
			out.write(func.toGraph());
			
			out.close();
			
			Runtime r = Runtime.getRuntime();
			
			String[] cmds = {"/bin/sh", "-c", "/opt/local/bin/dot -Tjpg "+System.getProperty("user.home")+"/"+filename+".dot -o "+System.getProperty("user.home")+"/"+filename+".jpg"};
			
			Process proc = r.exec(cmds);
			
			InputStream stderr = proc.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            
            String line = null;
            while((line = br.readLine()) != null) {
            	System.out.println(line);
            }
            
            BufferedReader brerr = new BufferedReader(isr);
            while((line = brerr.readLine()) != null) {
                System.err.println(line);
            }
            
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		
		
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
		//String optimizedCode = optimization.optimizeCodeFromFile("input/llvm_constant_folding1");
		//String optimizedCode = optimization.optimizeCodeFromFile("input/llvm_cf_prop_deadb");
		String optimizedCode = optimization.optimizeCodeFromFile("input/llvm_lebendigkeit_global1");
		System.out.println(optimizedCode);
	}

}
