package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
* Syntax:
  
  store [volatile] <ty> <value>, <ty>* <pointer>[, align <alignment>][, !nontemporal !<index>]        ; yields {void}
  store atomic [volatile] <ty> <value>, <ty>* <pointer> [singlethread] <ordering>, align <alignment>  ; yields {void}
*/

public class LLVM_StoreCommand extends LLVM_GenericCommand{
	private boolean vol = false;
	private boolean atom = false;
	
	public LLVM_StoreCommand(String cmdLine, ILLVM_Command predecessor, ILLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.STORE);
		// Kommentar entfernen
		if (cmdLine.contains(";")) cmdLine = cmdLine.substring(0, cmdLine.indexOf(";"));
		
		// store entfernen
		cmdLine = cmdLine.substring(cmdLine.indexOf("store ") + 5).trim();
		
		// atomic einlesen
		if (cmdLine.startsWith("atomic")){
			this.atom = true;
			cmdLine = cmdLine.substring(cmdLine.indexOf("atomic ") + 6).trim();
		}
		
		// volatile einlesen
		if (cmdLine.startsWith("volatile")){
			this.vol = true;
			cmdLine = cmdLine.substring(cmdLine.indexOf("volatile ") + 8).trim();
		}
		
		for(String pair : cmdLine.split(",")){
			pair = pair.trim();
			if (pair.endsWith("\"")){
				// falls Inline-String
				int cutAt = pair.lastIndexOf(" ", pair.lastIndexOf("\"", pair.length()-1));
				operands.add(new LLVM_Parameter(pair.substring(cutAt).trim(), pair.substring(0, cutAt).trim()));
			}else{
				// sonst
				operands.add(new LLVM_Parameter(pair.substring(pair.lastIndexOf(" ")).trim(), pair.substring(0, pair.lastIndexOf(" ")).trim()));
			}
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		String cmd_out = "store ";
		
		if (atom) cmd_out += "atomic ";
		if (vol) cmd_out += "volatile ";
		
		cmd_out += operands.get(0).getTypeString()+" ";
		cmd_out += operands.get(0).getName();
		
		for (int i = 1; i < operands.size(); i++){
			cmd_out += ", " + operands.get(i).getTypeString() + " ";
			cmd_out += operands.get(i).getName();
		}
		
		cmd_out += " " + getComment();
		
		return cmd_out;
	}
}