package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 * Syntax:

  ret <type> <value>       ; Return a value from a non-void function
  ret void                 ; Return from void function

 */

public class LLVM_ReturnCommand extends LLVM_GenericCommand{
	
	public LLVM_ReturnCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		
		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		parseEraseString(cmd, "ret");
		
		if (parseOptionalString(cmd, "void")){
			setOperation(LLVM_Operation.RET);
			operands.add(new LLVM_Parameter("void", "void"));
		}else{
			setOperation(LLVM_Operation.RET_CODE);
			String type = parseReadType(cmd);
			String value = parseReadValue(cmd);
			operands.add(new LLVM_Parameter(value, type));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		if (operands == null || operands.size() < 1) return null;
		
		String cmd_output = "ret ";
		
		switch(operation){
			case RET :
				cmd_output += operands.get(0).getTypeString() + " ";
				break;
			case RET_CODE :
				cmd_output += operands.get(0).getTypeString() + " ";
				cmd_output += operands.get(0).getName() + " ";
				break;
			default:
				return "";
		}
		
		cmd_output += " " + getComment();
		
		return cmd_output;
	}
}
