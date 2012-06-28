package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 * Kommentarzeilen
 */

public class LLVM_DummyCommand extends LLVM_GenericCommand{
	
	public LLVM_DummyCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.DUMMY);
		this.command = cmdLine;
		if (LLVM_Optimization.DEBUG) System.out.println("Dummy generiert: " + this.toString());
	}
	
	public String toString() {
		return getCommand();
	}
}
