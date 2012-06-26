package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
* Syntax:
  
  <result> = [tail] call [cconv] [ret attrs] <ty> [<fnty>*] <fnptrval>(<function args>) [fn attrs]
*/

public class LLVM_CallCommand extends LLVM_GenericCommand{
	private boolean tail = false;
	private String cconv = "";
	private String reta = "";
	private String fnty = "";
	private String fnptrval = "";
	private String fnattrs = "";
	
	public LLVM_CallCommand(String cmdLine, ILLVM_Command predecessor, ILLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.CALL);
		
		String result = cmdLine.substring(0, cmdLine.indexOf("=")).trim();
		if (cmdLine.contains(" tail ")) tail = true;
		
		// tail und call entfernen
		cmdLine = cmdLine.substring(cmdLine.indexOf("call ") + 4).trim();
		
		// cconv einlesen
		if (cmdLine.startsWith("ccc") ||
			cmdLine.startsWith("fastcc") || 
			cmdLine.startsWith("coldcc") ||
			cmdLine.startsWith("cc")){
			this.cconv = cmdLine.substring(0, cmdLine.indexOf(" ", 3)).trim();
			cmdLine = cmdLine.substring(cmdLine.indexOf(" ", 3)).trim();
		}
		
		// ret attrs einlesen
		while (cmdLine.startsWith("zeroext") ||
				cmdLine.startsWith("signext") ||
				cmdLine.startsWith("inreg")){
			this.reta += cmdLine.substring(0, cmdLine.indexOf(" ", 4)) + " ";
			cmdLine = cmdLine.substring(cmdLine.indexOf(" ", 4)).trim();
		}

		// ty einlesen
		String ty = cmdLine.substring(0, cmdLine.indexOf("("));
		ty = (cmdLine.indexOf("@") > ty.length())?ty:cmdLine.substring(0, cmdLine.indexOf("@"));
		ty = ty.trim();
		cmdLine = cmdLine.substring(ty.length()).trim();
		
		// fnty einlesen
		if (!cmdLine.startsWith("@")){
			this.fnty = cmdLine.substring(0, cmdLine.indexOf("@")).trim();
			cmdLine = cmdLine.substring(this.fnty.length()).trim();
		}
		
		// fnptrval einlesen
		this.fnptrval = cmdLine.substring(0, cmdLine.indexOf("(")).trim();
		cmdLine = cmdLine.substring(this.fnptrval.length()).trim();
		
		// function args einlesen
		String funcargs = cmdLine.substring(1, cmdLine.indexOf(")"));
		cmdLine = cmdLine.substring(funcargs.length()+2).trim();
		
		for(String pair : funcargs.split(",")){
			operands.add(new LLVM_Parameter(pair.substring(pair.lastIndexOf(" ")).trim(), pair.substring(0, pair.lastIndexOf(" ")).trim()));
		}
		
		// fn attrs einlesen
		while (cmdLine.startsWith("noreturn") ||
				cmdLine.startsWith("nounwind") ||
				cmdLine.startsWith("readnone") ||
				cmdLine.startsWith("readonly")){
			this.fnattrs += cmdLine.substring(0, cmdLine.indexOf(" ", 7)) + " ";
			cmdLine = cmdLine.substring(cmdLine.indexOf(" ", 7)).trim() + " ";
		}
		
		// <result> <ty>
		this.target = new LLVM_Parameter(result, ty);
		this.fnattrs = this.fnattrs.trim();
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		String cmd_out = target.getName() + " = ";
		
		if (tail) cmd_out += "tail ";
		cmd_out += "call ";
		if (cconv != "") cmd_out += cconv + " ";
		if (reta != "") cmd_out += reta + " ";
		
		cmd_out += target.getTypeString() + " ";
		
		if (fnty != "") cmd_out += fnty + " ";
		if (fnptrval != "") cmd_out += fnptrval;
		
		if (operands.size() > 0){
			cmd_out += "(";
			for (int i = 0; i < operands.size(); i++){
				cmd_out += operands.get(i).getTypeString() + " " + operands.get(i).getName();
				if (i+1 < operands.size()) cmd_out += ", "; 
			}
			cmd_out += ") ";
		}
		
		if (fnattrs != "") cmd_out += fnattrs + " ";
		
		cmd_out += " " + getComment();
		
		return cmd_out;
	}
}