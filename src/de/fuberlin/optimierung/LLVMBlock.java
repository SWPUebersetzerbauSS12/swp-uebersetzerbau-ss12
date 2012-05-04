package de.fuberlin.optimierung;

import java.util.LinkedList;

class LLVMBlock implements ILLVMBlock{
	
	// Erster und letzter Befehl des Blockes
	private LLVMCommand firstCommand = null;
	private LLVMCommand lastCommand = null;

	// Ursprüngliches Label des Blockes
	private String label = "";

	// Vorgaenger- und Nachfolgerbloecke
	// Hieraus entsteht der Flussgraph zwischen den Bloecken
	private LinkedList<LLVMBlock> children;
	private LinkedList<LLVMBlock> parents;
	
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

	private void createDAG() {

	}

	private void createCommands() {

		String commandsArray[] = this.blockCode.split("\n");
		this.firstCommand = new LLVMCommand(commandsArray[0],null);
		LLVMCommand predecessor = this.firstCommand;
		for(int i=1; i<commandsArray.length; i++) {
			LLVMCommand c = new LLVMCommand(commandsArray[i],predecessor);
			predecessor = c;
		}
		this.lastCommand = predecessor;

	}

}
