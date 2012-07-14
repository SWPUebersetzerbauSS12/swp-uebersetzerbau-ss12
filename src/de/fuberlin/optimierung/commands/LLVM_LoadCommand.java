package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
* Syntax;
  
  <result> = load [volatile] <ty>* <pointer>[, align <alignment>][, !nontemporal !<index>][, !invariant.load !<index>]
  <result> = load atomic [volatile] <ty>* <pointer> [singlethread] <ordering>, align <alignment>
  !<index> = !{ i32 1 }
*/

public class LLVM_LoadCommand extends LLVM_GenericCommand{
	private boolean vol = false;
	private boolean atom = false;
	
	public LLVM_LoadCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.LOAD);
		
		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		String result = parseReadResult(cmd);
		parseOptionalString(cmd, "load");
		
		atom = parseOptionalString(cmd, "atomic");
		vol = parseOptionalString(cmd, "volatile");
		
		String ty = parseReadType(cmd);
		target = new LLVM_Parameter(result, ty);
		
		String pointer = parseReadValue(cmd);
		operands.add(new LLVM_Parameter(pointer, ty));
		
		while (parseEraseString(cmd, ",")){
			String typ = parseReadType(cmd);
			String name = parseReadValue(cmd);
			operands.add(new LLVM_Parameter(name, typ));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		if (target == null || operands == null || operands.size() < 1) return null;
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