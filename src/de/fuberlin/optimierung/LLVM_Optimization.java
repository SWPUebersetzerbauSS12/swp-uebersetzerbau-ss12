package de.fuberlin.optimierung;

import java.io.*;
import java.util.LinkedList;

public class LLVM_Optimization implements ILLVM_Optimization {
	
	private String code = "";
	private String beforeFunc = "";
	
	private LinkedList<LLVM_Function> functions;
	
	public static final boolean DEBUG = false;
	
	public LLVM_Optimization(){
		functions = new LinkedList<LLVM_Function>();
	}
	
	private void parseCode() {
		
		// Splitte in Funktionen
		String[] functions = this.code.split("define ");
		this.beforeFunc = functions[0];
		for (int i = 1; i < functions.length; i++) {
			this.functions.add(new LLVM_Function(functions[i]));
		}
	}

	private String optimizeCode() {
		// Code steht als String in this.code
		// Starte Optimierung
		this.parseCode();
		
		String outputLLVM = this.beforeFunc;
		
		System.out.println("Before optimization\n"+getStatistic());
		
		// Gehe Funktionen durch
		for(LLVM_Function tmp : this.functions) {
	
			// Erstelle Flussgraph
			tmp.createFlowGraph();
			
			// Optimierungsfunktionen
			tmp.createRegisterMaps();
			
			//Constant Folding
			tmp.constantFolding();
			
			// Reaching vor Lebendigkeitsanalyse
			// Koennen tote Stores entstehen
			tmp.reachingAnalysis();
			
			// Dead register elimination
			tmp.eliminateDeadRegisters();
			tmp.eliminateDeadBlocks();
			
			// CommonExpressions
			tmp.removeCommonExpressions();
			
			// Globale Lebendigkeitsanalyse fuer Store, Load
			tmp.globalLiveVariableAnalysis();
			
			// Entferne Bloecke, die nur unbedingten Sprungbefehl enthalten
			tmp.deleteEmptyBlocks();
			
			// Optimierte Ausgabe
			tmp.updateUnnamedLabelNames();
			outputLLVM += tmp.toString();
			
			//createGraph("func"+i, tmp);
		}
		
		System.out.println("After optimization\n"+getStatistic());
		
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
	
	public String getCode(){
		return this.code;
	}
	
	private int getBlockCount(){
		int count = 0;
		for (LLVM_Function f : functions) {
			count += f.getBlocks().size();
		}
		return count;
	}
	
	private int getCommandsCount(){
		int count = 0;
		for (LLVM_Function f : functions) {
			
			for(LLVM_Block c : f.getBlocks()) {
				count += c.countCommands();
			}
			
		}
		return count;
	}
	
	public String getStatistic(){
		
		String out = "";
		out += "############################\n";
		out += "Count Functions: "+functions.size()+"\n";
		out += "Count Blocks: "+getBlockCount()+"\n";
		out += "Count Commands: "+getCommandsCount()+"\n";
		out += "############################\n";
		return out;
	}
	
	public static void main(String args[]) {
		
		if(args.length>0) {
			ILLVM_Optimization optimization = new LLVM_Optimization();
			String optimizedCode = optimization.optimizeCodeFromFile(args[0]);
			System.out.println(optimizedCode);
		}
		else {

			ILLVM_Optimization optimization = new LLVM_Optimization();
			//String optimizedCode = optimization.optimizeCodeFromFile("input/de/fuberlin/optimierung/llvm_test.llvm");
			//String optimizedCode = optimization.optimizeCodeFromFile("input/de/fuberlin/optimierung/llvm_constant_folding1");
			//String optimizedCode = optimization.optimizeCodeFromFile("input/de/fuberlin/optimierung/llvm_cf_prop_deadb");
			//String optimizedCode = optimization.optimizeCodeFromFile("input/de/fuberlin/optimierung/llvm_lebendigkeit_global1");
			//String optimizedCode = optimization.optimizeCodeFromFile("input/de/fuberlin/optimierung/llvm_dag");
			//String optimizedCode = optimization.optimizeCodeFromFile("input/de/fuberlin/optimierung/llvm_dead_block");
			//String optimizedCode = optimization.optimizeCodeFromFile("input/de/fuberlin/optimierung/llvm_localsub_registerprop");
			//String optimizedCode = optimization.optimizeCodeFromFile("input/de/fuberlin/optimierung/llvm_array");
			//String optimizedCode = optimization.optimizeCodeFromFile("input/de/fuberlin/optimierung/llvm_parsertest1");
			//String optimizedCode = optimization.optimizeCodeFromFile("input/de/fuberlin/optimierung/test.ll");
			String optimizedCode = optimization.optimizeCodeFromFile("input/de/fuberlin/optimierung/test3.s");//test_new.ll");
	
			System.out.println("###########################################################");
			System.out.println("################## Optimization Input #####################");
			System.out.println("###########################################################");
			System.out.println(optimization.getCode());
			
			System.out.println("###########################################################");
			System.out.println("################## Optimization Output ####################");
			System.out.println("###########################################################");
			System.out.println(optimizedCode);
		}
	}

}
