package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
* Syntax;
  
  <result> = load [volatile] <ty>* <pointer>[, align <alignment>][, !nontemporal !<index>][, !invariant.load !<index>]
  <result> = load atomic [volatile] <ty>* <pointer> [singlethread] <ordering>, align <alignment>
  !<index> = !{ i32 1 }
*/

public class LLVM_LoadCommand extends LLVM_GenericCommand{
	
	public LLVM_LoadCommand(String[] cmd, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		
		// <result> <ty>*
		target = new LLVM_Parameter(cmd[0], cmd[3]);

		// optionale Parameter
		for (int j = 3; (j + 1 < cmd.length); j = j + 2){
			// <ty> <pointer>
			operands.add(new LLVM_Parameter(cmd[j+1], cmd[j]));
		}
		
		System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		String cmd_out = target.getName()+" = ";
		cmd_out += "load ";
		
		cmd_out += operands.get(0).getTypeString();
		
		for (int i = 0; i < operands.size(); i++){
			cmd_output += ", " + operands.get(i).getTypeString() + " ";
			cmd_output += operands.get(i).getName();
		}
		
		cmd_out += " " + getComment();
		return cmd_out;
	}
}