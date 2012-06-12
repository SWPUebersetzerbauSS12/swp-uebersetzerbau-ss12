package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
* Syntax:
  
  store [volatile] <ty> <value>, <ty>* <pointer>[, align <alignment>][, !nontemporal !<index>]        ; yields {void}
  store atomic [volatile] <ty> <value>, <ty>* <pointer> [singlethread] <ordering>, align <alignment>  ; yields {void}
*/

public class LLVM_StoreCommand extends LLVM_GenericCommand{
	private boolean vol = false;
	private boolean atom = false;
	
	public LLVM_StoreCommand(String[] cmd, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		
		if (cmd[1].trim().equals("atomic")){
			atom = true;
			if (cmd[2].trim().equals("volatile")) vol = true;
		}else{
			if (cmd[1].trim().equals("volatile")) vol = true;
		}
		
		int start = 1;
		start = (atom) ? start + 1 : start;
		start = (vol) ? start + 1 : start;
		
		// <value> <ty>
		operands.add(new LLVM_Parameter(cmd[start+1], cmd[start]));
		for (int j = start + 2; (j + 1 < cmd.length); j = j + 2){
			// <ty> <pointer>
			operands.add(new LLVM_Parameter(cmd[j+1], cmd[j]));
		}
		
		System.out.println("Operation generiert: " + this.toString());
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