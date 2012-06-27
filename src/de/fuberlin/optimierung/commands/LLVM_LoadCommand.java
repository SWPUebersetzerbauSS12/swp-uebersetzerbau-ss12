package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
* Syntax;
  
  <result> = load [volatile] <ty>* <pointer>[, align <alignment>][, !nontemporal !<index>][, !invariant.load !<index>]
  <result> = load atomic [volatile] <ty>* <pointer> [singlethread] <ordering>, align <alignment>
  !<index> = !{ i32 1 }
*/

public class LLVM_LoadCommand extends LLVM_GenericCommand{
	private boolean vol = false;
	private boolean atom = false;
	
	public LLVM_LoadCommand(String cmdLine, ILLVM_Command predecessor, ILLVM_Block block){
		super(predecessor, block, cmdLine);
		
		setOperation(LLVM_Operation.LOAD);
		// Kommentar entfernen
		if (cmdLine.contains(";")) cmdLine = cmdLine.substring(0, cmdLine.indexOf(";"));
		
		// result einlesen
		String result = cmdLine.substring(0, cmdLine.indexOf("=")).trim();
		cmdLine = cmdLine.substring(cmdLine.indexOf("load ") + 4).trim();
		
		// atomic einlesen
		if (cmdLine.startsWith("atomic ")){
			atom = true;
			cmdLine = cmdLine.substring(cmdLine.indexOf("atomic ") + 6).trim();
		}
		
		// volatile einlesen
		if (cmdLine.startsWith("volatile ")){
			vol = true;
			cmdLine = cmdLine.substring(cmdLine.indexOf("volatile ") + 8).trim();
		}
		
		// ty einlesen
		String ty = "";
		if (cmdLine.contains(",")) ty = cmdLine.substring(0, cmdLine.lastIndexOf(" ", cmdLine.indexOf(","))).trim();
		else ty = cmdLine.substring(0, cmdLine.lastIndexOf(" ")).trim();
		target = new LLVM_Parameter(result, ty);
		
		String[] comma = cmdLine.split(",");
		for (int i = 0; i < comma.length; i++){
			int cutAt = comma[i].lastIndexOf(" ");
			operands.add(new LLVM_Parameter(comma[i].substring(cutAt).trim(), comma[i].substring(0, cutAt).trim()));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		String cmd_out = target.getName() + " = ";
		cmd_out += "load ";
		
		if (atom) cmd_out += "atomic ";
		if (vol) cmd_out += "volatile ";
		
		cmd_out += operands.get(0).getTypeString() + " ";
		cmd_out += operands.get(0).getName();
		
		for (int i = 1; i < operands.size(); i++){
			cmd_out += ", " + operands.get(i).getTypeString() + " ";
			cmd_out += operands.get(i).getName();
		}
		
		cmd_out += " " + getComment();
		return cmd_out;
	}
}