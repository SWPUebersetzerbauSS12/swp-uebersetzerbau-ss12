package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;

/*
 * Kommentarzeilen
 */

public class LLVM_DummyCommand extends LLVM_GenericCommand{
	
	public LLVM_DummyCommand(String cmdLine, ILLVM_Command predecessor, ILLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.DUMMY);
		this.command = cmdLine;
		if (LLVM_Optimization.DEBUG) System.out.println("Dummy generiert: " + this.toString());
	}
	
	public String toString() {
		return getCommand();
	}
}
