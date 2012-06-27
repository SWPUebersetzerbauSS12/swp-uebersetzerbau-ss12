package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
 *  Syntax:
 *	<result> = getelementptr <pty>* <ptrval>{, <ty> <idx>}*
 *	<result> = getelementptr inbounds <pty>* <ptrval>{, <ty> <idx>}*
 */

public class LLVM_GetElementPtrCommand extends LLVM_GenericCommand {
	
	boolean hasInbounds = false;
	
	public LLVM_GetElementPtrCommand(String cmdLine, ILLVM_Command predecessor, ILLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.GETELEMENTPTR);
		// Kommentar entfernen
		if (cmdLine.contains(";")) cmdLine = cmdLine.substring(0, cmdLine.indexOf(";"));
		
		// result einlesen
		String result = cmdLine.substring(0, cmdLine.indexOf("=")).trim();
		cmdLine = cmdLine.substring(cmdLine.indexOf("getelementptr ") + 13).trim();
		
		// inbounds einlesen
		if (cmdLine.startsWith("inbounds ")){
			hasInbounds = true;
			cmdLine = cmdLine.substring(cmdLine.indexOf("inbounds ") + 8).trim();
		}
		
		// ty einlesen
		String ty = "";
		if (cmdLine.contains(",")) ty = cmdLine.substring(0, cmdLine.lastIndexOf(" ", cmdLine.indexOf(","))).trim();
		else ty = cmdLine.substring(0, cmdLine.lastIndexOf(" ")).trim();
		target = new LLVM_Parameter(result, ty);
		
		String[] comma = cmdLine.split(",");
		for (int i = 0; i < comma.length; i++){
			int cutAt = comma[i].lastIndexOf(" ");
			operands.add(new LLVM_Parameter(comma[i].substring(cutAt).trim(), comma[i].substring(0, cutAt).trim()));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString(){
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
