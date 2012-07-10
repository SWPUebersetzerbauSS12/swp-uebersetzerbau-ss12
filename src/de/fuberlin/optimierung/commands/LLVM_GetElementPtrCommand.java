package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 *  Syntax:
 *	<result> = getelementptr <pty>* <ptrval>{, <ty> <idx>}*
 *	<result> = getelementptr inbounds <pty>* <ptrval>{, <ty> <idx>}*
 */

public class LLVM_GetElementPtrCommand extends LLVM_GenericCommand {
	
	boolean hasInbounds = false;
	
	public LLVM_GetElementPtrCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.GETELEMENTPTR);

		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		String result = parseReadResult(cmd);
		parseEraseString(cmd, "getelementptr");
		
		if (parseOptionalString(cmd, "inbounds")) hasInbounds = true;
		
		String pty = parseReadPointer(cmd);
		String ptyval = parseReadValue(cmd);
		
		target = new LLVM_Parameter(result, pty);
		operands.add(new LLVM_Parameter(ptyval, pty));
		
		while (parseEraseString(cmd, ",")){
			String ty = parseReadType(cmd);
			String idx = parseReadValue(cmd);
			operands.add(new LLVM_Parameter(idx, ty));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString(){
		if (target == null || operands == null || operands.size() < 1) return null;
		
		String cmd_out = target.getName() + " = ";
		cmd_out += "getelementptr ";
		
		if (hasInbounds) cmd_out += "inbounds ";
		
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
