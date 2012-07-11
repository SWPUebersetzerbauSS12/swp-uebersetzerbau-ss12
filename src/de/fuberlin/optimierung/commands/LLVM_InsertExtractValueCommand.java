package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 *  Syntax:
 *  <result> = insertvalue <aggregate type> <val>, <ty> <elt>, <idx>{, <idx>}*    ; yields <aggregate type>
 *	<result> = extractvalue <aggregate type> <val>, <idx>{, <idx>}*
 */

public class LLVM_InsertExtractValueCommand extends LLVM_GenericCommand {
	
	public LLVM_InsertExtractValueCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		
		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		String result = parseReadResult(cmd);
		String cond = parseReadValue(cmd);
		
		if (cond.startsWith("insertvalue")) setOperation(LLVM_Operation.INSERTVALUE);
		if (cond.startsWith("extractvalue")) setOperation(LLVM_Operation.EXTRACTVALUE);
		
		String aggr = parseReadType(cmd);
		String val = parseReadValue(cmd);
		parseEraseString(cmd, ",");
		
		target = new LLVM_Parameter(result, aggr);
		operands.add(new LLVM_Parameter(val, aggr));
		
		if (this.getOperation() == LLVM_Operation.INSERTVALUE){
			String ty = parseReadType(cmd);
			String elt = parseReadValue(cmd);
			parseEraseString(cmd, ",");
			operands.add(new LLVM_Parameter(elt, ty));
		}
		
		// alle idx einlesen
		while(cmd.toString().length() > 0){
			String idx = parseReadValue(cmd);
			parseEraseString(cmd, ",");
			operands.add(new LLVM_Parameter(idx, "i32"));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString(){
		if (target == null || operands == null) return null;
		
		String cmd_out = target.getName() + " = ";
		if (this.getOperation() == LLVM_Operation.INSERTVALUE){
			cmd_out += "insertvalue ";
		}else{
			cmd_out += "extractvalue ";
		}
		int start = 0;
		
		cmd_out += operands.get(start).getTypeString() + " ";
		cmd_out += operands.get(start).getName();
		
		start++;
		if (this.getOperation() == LLVM_Operation.INSERTVALUE){
			cmd_out += ", " + operands.get(start).getTypeString() + " ";
			cmd_out += operands.get(start).getName();
			start++;
		}
		
		for (int i = start; i < operands.size(); i++){
			cmd_out += ", " + operands.get(i).getName();
		}
		
		cmd_out += " " + getComment();
		return cmd_out;
	}
}
