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
		// Kommentar entfernen
		if (cmdLine.contains(";")) cmdLine = cmdLine.substring(0, cmdLine.indexOf(";"));
		
		if (cmdLine.contains("insertvalue ")) setOperation(LLVM_Operation.INSERTVALUE);
		if (cmdLine.contains("extractvalue ")) setOperation(LLVM_Operation.EXTRACTVALUE);
		
		// result einlesen
		String result = cmdLine.substring(0, cmdLine.indexOf("=")).trim();
		if (this.getOperation() == LLVM_Operation.INSERTVALUE){
			cmdLine = cmdLine.substring(cmdLine.indexOf("insertvalue ") + 11).trim();
		}else{
			cmdLine = cmdLine.substring(cmdLine.indexOf("extractvalue ") + 12).trim();
		}
		
		int count = getComplexStructEnd(cmdLine);
		
		// aggr einlesen
		String aggr = cmdLine.substring(0, count+1).trim();
		cmdLine = cmdLine.substring(count+1).trim();
		
		// val einlesen
		String val = cmdLine.substring(0, cmdLine.indexOf(",")).trim();
		cmdLine = cmdLine.substring(cmdLine.indexOf(",")+1).trim();
		
		target = new LLVM_Parameter(result, aggr);
		operands.add(new LLVM_Parameter(val, aggr));
		
		if (this.getOperation() == LLVM_Operation.INSERTVALUE){
			// ty einlesen
			String ty = cmdLine.substring(0, cmdLine.lastIndexOf(" ", cmdLine.indexOf(","))).trim();
			cmdLine = cmdLine.substring(cmdLine.lastIndexOf(" ", cmdLine.indexOf(","))).trim();
			
			// elt einlesen
			String elt = cmdLine.substring(0, cmdLine.indexOf(",")).trim();
			cmdLine = cmdLine.substring(cmdLine.indexOf(",")+1).trim();
			operands.add(new LLVM_Parameter(elt, ty));
		}
		
		// alle idx einlesen
		String[] comma = cmdLine.split(",");
		for (int i = 0; i < comma.length; i++){
			operands.add(new LLVM_Parameter(comma[i].trim(), "i32"));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString(){
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
