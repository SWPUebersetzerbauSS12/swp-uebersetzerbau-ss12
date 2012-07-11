package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
* Syntax:
  
  store [volatile] <ty> <value>, <ty>* <pointer>[, align <alignment>][, !nontemporal !<index>]        ; yields {void}
  store atomic [volatile] <ty> <value>, <ty>* <pointer> [singlethread] <ordering>, align <alignment>  ; yields {void}
*/

public class LLVM_StoreCommand extends LLVM_GenericCommand{
	private boolean vol = false;
	private boolean atom = false;
	
	public LLVM_StoreCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.STORE);

		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		parseOptionalString(cmd, "store");
		
		atom = parseOptionalString(cmd, "atomic");
		vol = parseOptionalString(cmd, "volatile");
		
		String ty = parseReadType(cmd);
		String value = parseReadValue(cmd);
		parseEraseString(cmd, ",");
		String typ = parseReadType(cmd);
		String pointer = parseReadValue(cmd);
		
		operands.add(new LLVM_Parameter(value, ty));
		operands.add(new LLVM_Parameter(pointer, typ));
		
		while (parseEraseString(cmd, ",")){
			String type = parseReadType(cmd);
			String name = parseReadValue(cmd);
			operands.add(new LLVM_Parameter(name, type));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		if (operands == null || operands.size() < 1) return null;
		
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