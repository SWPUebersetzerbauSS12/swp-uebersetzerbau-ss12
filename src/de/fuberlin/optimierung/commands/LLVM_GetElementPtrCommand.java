package de.fuberlin.optimierung.commands;

import java.util.ArrayList;
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
		
		String[] cmd = command.split("[ \t]");
		int i = 3;
		
		if(cmd[3].compareTo("inbounds") == 0){
			hasInbounds = true;
			i++;
		}
		
		// <result> <ty>
		target = new LLVM_Parameter(cmd[0], cmd[4]);
		
		ArrayList<String> rest_cmd = new ArrayList<String>();
		
		for(int j = i; j < cmd.length;j++){
			rest_cmd.add(cmd[j]);
		}
		
		if(cmd[i].contains("[")){
			operands.add(readArrayListToLLVM_Parameter(rest_cmd, parseTypes.array, false));
			if (operands.get(0) == null){
				operands.remove(0);
			}
		}

		while (rest_cmd.size() % 2 == 0 && rest_cmd.size() >= 2){
			operands.add(new LLVM_Parameter(rest_cmd.get(1), rest_cmd.get(0)));
			rest_cmd.remove(0);
			rest_cmd.remove(0);
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
