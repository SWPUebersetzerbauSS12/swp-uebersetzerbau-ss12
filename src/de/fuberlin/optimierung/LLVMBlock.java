package de.fuberlin.optimierung;

import java.util.LinkedList;

import de.fuberlin.optimierung.commands.LLVM_ArithmeticCommand;
import de.fuberlin.optimierung.commands.LLVM_GenericCommand;

class LLVMBlock implements ILLVMBlock {
	
	// Erster und letzter Befehl des Blockes
	private ILLVMCommand firstCommand = null;
	private ILLVMCommand lastCommand = null;

	// Ursprüngliches Label des Blockes
	private String label = "";

	// Vorgaenger- und Nachfolgerbloecke
	// Hieraus entsteht der Flussgraph zwischen den Bloecken
	private LinkedList<ILLVMBlock> children;
	private LinkedList<ILLVMBlock> parents;
	
	// Kompletter Code des Blocks als String
	private String blockCode;

	public LLVMBlock(String blockCode) {
	
		children = new LinkedList<ILLVMBlock>();
		parents = new LinkedList<ILLVMBlock>();
		
		this.blockCode = blockCode;
		System.out.println(blockCode + "\n*****************\n");
		
		this.createCommands();

	}

	public void optimizeBlock() {

	}

	public void deleteBlock() {

	}

	private void createDAG() {

	}

	private void createCommands() {
		String commandsArray[] = this.blockCode.split("\n");
		this.firstCommand = mapCommands(commandsArray[0], null);
		
		ILLVMCommand predecessor = this.firstCommand;
		for(int i=1; i<commandsArray.length; i++) {
			ILLVMCommand c = mapCommands(commandsArray[i],predecessor);
			predecessor = c;
		}
		this.lastCommand = predecessor;
	}
	
	// Ermittelt Operation und erzeugt Command mit passender Klasse
	//TODO elegante Methode finden, switch funktioniert auf Strings nicht!
	private LLVM_GenericCommand mapCommands(String cmdLine, ILLVMCommand predecessor){
		String[] cmd = cmdLine.trim().split(" ");
		if (cmd.length > 0){
			if (cmd.length > 3 && cmd[1].equals("=")){
				
				if (cmd[2].compareTo("add") == 0){
					return new LLVM_ArithmeticCommand(cmd, LLVMOperation.ADD, predecessor, this);
				}else if(cmd[2].compareTo("sub") == 0){
					return new LLVM_ArithmeticCommand(cmd, LLVMOperation.SUB, predecessor, this);
				}else if(cmd[2].compareTo("mul") == 0){
					return new LLVM_ArithmeticCommand(cmd, LLVMOperation.MUL, predecessor, this);
				}else if(cmd[2].compareTo("div") == 0){
					return new LLVM_ArithmeticCommand(cmd, LLVMOperation.DIV, predecessor, this);
				}
			}
		}
		return null;
	}

	public void setFirstCommand(ILLVMCommand first) {
		this.firstCommand = first;
	}

	public void setLastCommand(ILLVMCommand last) {
		this.lastCommand = last;
	}
	
	public String toString() {
		String code = label+"\n";
		
		ILLVMCommand tmp = firstCommand;
		while(tmp != null && !tmp.equals(lastCommand)){
			code += tmp.toString();
			tmp = tmp.getSuccessor();
		}
		code += "\n";
		
		for (ILLVMBlock block : children) {
			code += block.toString();
		}
		
		return code;
	}
}
