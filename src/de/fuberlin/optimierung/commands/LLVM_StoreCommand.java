package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
* Syntax:
  
  store [volatile] <ty> <value>, <ty>* <pointer>[, align <alignment>][, !nontemporal !<index>]        ; yields {void}
  store atomic [volatile] <ty> <value>, <ty>* <pointer> [singlethread] <ordering>, align <alignment>  ; yields {void}
*/

class LLVM_StoreCommand extends LLVM_GenericCommand{
	
	public LLVM_StoreCommand(String[] cmd, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		// init operands
		//operands = new LinkedList<LLVM_Parameter>();
		
		// speichert den Typ und den Namen der Zieladresse
		//target = new LLVM_Parameter(cmd[4], cmd[3]);
		// speichert den Typ und den Wert den store speichert
		//operands = new LLVM_Parameter(cmd[2], cmd[1]);
		
		
		System.out.println("Operands generiert: ");
		System.out.println(this.toString());
	}
	
	public String toString() {
		String cmd_out = "store ";
		
		//cmd_out += operands.get(0).getTypeName()+" ";
		cmd_out += operands.get(0).getName()+" ";
		
		//cmd_out += target.getTypeName()+" ";
		cmd_out += target.getName();
		
		//cmd_out += getComment;
		
		return cmd_out;
	}
}