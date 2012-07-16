package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 * Syntax:

  br i1 <cond>, label <iftrue>, label <iffalse>
  br label <dest>          ; Unconditional branch

 */

public class LLVM_BranchCommand extends LLVM_GenericCommand{
	
	public LLVM_BranchCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		
		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		parseEraseString(cmd, "br");
		
		if (parseEraseString(cmd, "i1")){
			setOperation(LLVM_Operation.BR_CON);
			String cond = parseReadValue(cmd);
			parseEraseString(cmd, ",");
			parseEraseString(cmd, "label");
			String iftrue = parseReadValue(cmd);
			parseEraseString(cmd, ",");
			parseEraseString(cmd, "label");
			String iffalse = parseReadValue(cmd);
			operands.add(new LLVM_Parameter(cond, "i1"));
			operands.add(new LLVM_Parameter(iftrue, "label"));
			operands.add(new LLVM_Parameter(iffalse, "label"));
		}else{
			setOperation(LLVM_Operation.BR);
			parseEraseString(cmd, "label");
			String dest = parseReadValue(cmd);
			operands.add(new LLVM_Parameter(dest, "label"));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		if (operands == null || (operation == LLVM_Operation.BR && operands.size() < 1) || (operation == LLVM_Operation.BR_CON && operands.size() < 3)) return null;
		String cmd_output = "br ";
		
		switch(operation){
			case BR :
				cmd_output += operands.get(0).getTypeString() + " ";
				cmd_output += operands.get(0).getName() + " ";
				break;
			case BR_CON :
				cmd_output += operands.get(0).getTypeString() + " ";
				cmd_output += operands.get(0).getName() + ", ";
				cmd_output += operands.get(1).getTypeString() + " ";
				cmd_output += operands.get(1).getName() + ", ";
				cmd_output += operands.get(2).getTypeString() + " ";
				cmd_output += operands.get(2).getName() + " ";
				break;
			default:
				return "";
		}
		
		cmd_output += " " + getComment();
		
		return cmd_output;
	}
}
