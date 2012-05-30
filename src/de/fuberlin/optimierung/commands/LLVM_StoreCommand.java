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
	
	public LLVM_StoreCommand(String[] cmd, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		
		// <value> <ty>
		operands.add(new LLVM_Parameter(cmd[2], cmd[1]));
		// <pointer> <ty>
		operands.add(new LLVM_Parameter(cmd[4], cmd[3]));
		
		System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		String cmd_out = "store ";
		
		cmd_out += operands.get(0).getTypeString()+" ";
		cmd_out += operands.get(0).getName()+" ";
		
		cmd_out += operands.get(1).getTypeString()+" ";
		cmd_out += operands.get(1).getName();
		
		cmd_out += " " + getComment();
		
		return cmd_out;
	}
}