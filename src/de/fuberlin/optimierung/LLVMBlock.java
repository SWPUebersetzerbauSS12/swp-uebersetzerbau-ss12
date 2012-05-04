package de.fuberlin.optimierung;

import java.util.LinkedList;

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
		this.createCommands();

	}

	public void optimizeBlock() {

	}

	public void deleteBlock() {

	}

	public void setFirstCommand(ILLVMCommand first) {
		this.firstCommand = first;
	}

	public void setLastCommand(ILLVMCommand last) {
		this.lastCommand = last;
	}

	private void createDAG() {

	}

	private void createCommands() {

		String commandsArray[] = this.blockCode.split("\n");
		this.firstCommand = new LLVMCommand(commandsArray[0],null,this);
		ILLVMCommand predecessor = this.firstCommand;
		for(int i=1; i<commandsArray.length; i++) {
			ILLVMCommand c = new LLVMCommand(commandsArray[i],predecessor,this);
			predecessor = c;
		}
		this.lastCommand = predecessor;

	}

}
