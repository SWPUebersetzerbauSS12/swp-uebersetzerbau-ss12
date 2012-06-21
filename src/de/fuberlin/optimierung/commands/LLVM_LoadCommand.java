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
		
		String[] cmd = command.split("[ \t]");
		if (cmd[3].trim().equals("atomic")){
			atom = true;
			if (cmd[4].trim().equals("volatile")) vol = true;
		}else{
			if (cmd[3].trim().equals("volatile")) vol = true;
		}
		
		int start = 3;
		start = (atom) ? start + 1 : start;
		start = (vol) ? start + 1 : start;
		
		// <result> <ty>*
		target = new LLVM_Parameter(cmd[0], cmd[start]);

		// optionale Parameter
		for (int j = start; (j + 1 < cmd.length); j = j + 2){
			// <ty> <pointer>
			operands.add(new LLVM_Parameter(cmd[j+1], cmd[j]));
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