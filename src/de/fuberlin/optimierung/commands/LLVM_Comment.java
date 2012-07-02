package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 * Kommentarzeilen
 */

public class LLVM_Comment extends LLVM_GenericCommand{
	
	public LLVM_Comment(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.COMMENT);
		if (LLVM_Optimization.DEBUG) System.out.println("Kommentar generiert: " + this.toString());
	}
	
	public String toString() {
		return getComment();
	}
}
