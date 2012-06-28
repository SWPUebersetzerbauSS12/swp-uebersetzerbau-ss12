package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;

/*
 * Kommentarzeilen
 */

public class LLVM_Comment extends LLVM_GenericCommand{
	
	public LLVM_Comment(String cmdLine, ILLVM_Command predecessor, ILLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.COMMENT);
		if (LLVM_Optimization.DEBUG) System.out.println("Kommentar generiert: " + this.toString());
	}
	
	public String toString() {
		return getComment();
	}
}
