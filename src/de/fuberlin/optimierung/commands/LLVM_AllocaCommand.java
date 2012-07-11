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
		
		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		String result = parseReadResult(cmd);
		parseEraseString(cmd, "alloca");
		String ty = parseReadType(cmd);
		
		// <result> <ty>
		target = new LLVM_Parameter(result, ty);
		
		while (parseEraseString(cmd, ",")){
			String typ = parseReadType(cmd);
			String name = parseReadValue(cmd);
			operands.add(new LLVM_Parameter(name, typ));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString(){
		if (target == null || operands == null) return null;
		
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
