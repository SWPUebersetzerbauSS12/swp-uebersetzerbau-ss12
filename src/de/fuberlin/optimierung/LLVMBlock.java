package de.fuberlin.optimierung;

import java.util.LinkedList;

import de.fuberlin.optimierung.commands.LLVM_Add;
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
					return new LLVM_Add(cmd, predecessor, this);
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
	
}
