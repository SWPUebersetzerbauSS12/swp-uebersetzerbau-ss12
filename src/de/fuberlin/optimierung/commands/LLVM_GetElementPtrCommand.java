package de.fuberlin.optimierung.commands;

import java.util.ArrayList;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;
import de.fuberlin.optimierung.LLVM_ParameterType;

/*
 *  Syntax:
 *	<result> = getelementptr <pty>* <ptrval>{, <ty> <idx>}*
 *	<result> = getelementptr inbounds <pty>* <ptrval>{, <ty> <idx>}*
 */

public class LLVM_GetElementPtrCommand extends LLVM_GenericCommand {
	
	boolean hasInbounds = false;
	
	public LLVM_GetElementPtrCommand(String[] cmd,LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		
		// <result> <ty>
		target = new LLVM_Parameter(cmd[0], cmd[3]);
		
		int i = 3;
		
		if(cmd[3].compareTo("inbounds") == 0){
			hasInbounds = true;
			i++;
		}
		
		ArrayList<String> rest_cmd = new ArrayList<String>();
		
		for(int j = i; j < cmd.length;j++){
			rest_cmd.add(cmd[j]);
		}
		
		if(cmd[i].contains("[")){
			operands.add(readArrayListToLLVM_Parameter(rest_cmd, parseTypes.array, false));
		}else{
			operands.add(new LLVM_Parameter(cmd[i+1], cmd[i]));
		}

		//TODO Hier weitere Parameter erkennen {, <ty> <idx>}* beliebig viele
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString(){
		
		String res = "";
		
		return res;
	}
}
