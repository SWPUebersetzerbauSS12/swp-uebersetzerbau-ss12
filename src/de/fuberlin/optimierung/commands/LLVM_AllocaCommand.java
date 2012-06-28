package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 * Syntax:

  <result> = alloca <type>[, <ty> <NumElements>][, align <alignment>]     ; yields {type*}:result
 */

public class LLVM_AllocaCommand extends LLVM_GenericCommand{
	
	public LLVM_AllocaCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.ALLOCA);
		// Kommentar entfernen
		if (cmdLine.contains(";")) cmdLine = cmdLine.substring(0, cmdLine.indexOf(";"));		
		
		String result = cmdLine.substring(0, cmdLine.indexOf("=")).trim();
		cmdLine = cmdLine.substring(cmdLine.indexOf("alloca ") + 6);
		String[] comma = cmdLine.split(",");

		// <result> <type>
		target = new LLVM_Parameter(result, comma[0].trim());
		
		for (int i = 1; i < comma.length; i++){
			int cutAt = comma[i].lastIndexOf(" ");
			// <ty> <num>
			operands.add(new LLVM_Parameter(comma[i].substring(cutAt).trim(), comma[i].substring(0, cutAt).trim()));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		String cmd_output = target.getName() + " = ";
		
		switch(operation){
			case ALLOCA :
				cmd_output += "alloca ";
				break;
			default:
				return "";
		}
		
		cmd_output += target.getTypeString();
		
		for (int i = 0; i < operands.size(); i++){
			cmd_output += ", " + operands.get(i).getTypeString() + " ";
			cmd_output += operands.get(i).getName();
		}
		
		cmd_output += " " + getComment();

		return cmd_output;
	}
}
