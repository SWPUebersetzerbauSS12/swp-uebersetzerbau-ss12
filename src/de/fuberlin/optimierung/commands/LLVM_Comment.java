package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
 * Kommentarzeilen
 */

public class LLVM_Comment extends LLVM_GenericCommand{
	
	public LLVM_Comment(String[] cmd, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		if (LLVM_Optimization.DEBUG) System.out.println("Kommentar generiert: " + this.toString());
	}
	
	public String toString() {
		return getComment();
	}
}
