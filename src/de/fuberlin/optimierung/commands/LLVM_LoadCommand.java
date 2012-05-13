package de.fuberlin.optimierung.commands;

import java.lang.annotation.Target;
import java.util.LinkedList;
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

class LLVM_LoadCommand extends LLVM_GenericCommand{
	
	public LLVM_LoadCommand(String[] cmd, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		// Init operands
		operands = new LinkedList<LLVM_Parameter>();
		
		// Typ und Name wohin geladen wird
		target = new LLVM_Parameter(cmd[0], cmd[3]);
		// Name aus welcher Adresse geladen wird
		operands = new LLVM_Parameter(cmd[4], cmd[3]);
		
		System.out.println("Operands generiert: ");
		System.out.println(this.toString());
	}
	
	public String toString() {
		String cmd_out = target.getName()+" = ";
		cmd_out += "load";
		
		cmd_out += operands.get(0).getTypeName()+"";
		cmd_out += operands.get(0).getName();
		
		cmd_out += getComment();
		return cmd_out;
	}
}