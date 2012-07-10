package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
* Syntax:
  
  <result> = [tail] call [cconv] [ret attrs] <ty> [<fnty>*] <fnptrval>(<function args>) [fn attrs]
*/

public class LLVM_CallCommand extends LLVM_GenericCommand{
	private boolean tail = false;
	private boolean startsnotwithcall = false;
	private String cconv = "";
	private String reta = "";
	private String fnty = "";
	private String fnptrval = "";
	private String fnattrs = "";
	
	public LLVM_CallCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.CALL);
		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		
		String result = "";
		if (!cmdLine.startsWith("call ")){
			startsnotwithcall = true;
			result = parseReadResult(cmd);
			tail = parseOptionalString(cmd, "tail");
		}
		parseEraseString(cmd, "call");
		cconv = parseOptionalListSingle(cmd, new String[]{"ccc", "fastcc", "coldcc", "cc "});
		reta = parseOptionalList(cmd, new String[]{"zeroext", "signext", "inreg"});
		String ty = parseReadType(cmd);
		if (cmd.toString().indexOf('@') > 0){
			// optionaler Pointer nur vorhanden, wenn @ nicht am Anfang steht
			fnty = parseReadType(cmd);
		}
		fnptrval = parseReadValue(cmd);
		parseEraseString(cmd, "(");
		String funcargs = parseStringUntil(cmd, ")");
		parseEraseString(cmd, ")");
		fnattrs = parseOptionalList(cmd, new String[]{"noreturn", "nounwind", "readnone", "readonly"});
		
		//func args verarbeiten
		if (funcargs.length() > 0){
			StringBuilder tmp = new StringBuilder(funcargs);
			do{
				String typ = parseReadType(tmp);
				String name = parseReadValue(tmp);
				operands.add(new LLVM_Parameter(name, typ));
			}while(parseEraseString(tmp, ","));
		}
		
		// <result> <ty>
		target = new LLVM_Parameter(result, ty);
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		if (target == null || operands == null) return null;
		
		String cmd_out = "";
		
		if (startsnotwithcall){
			cmd_out += target.getName() + " = ";
			if (tail) cmd_out += "tail ";
		}
		cmd_out += "call ";
		if (cconv != "") cmd_out += cconv + " ";
		if (reta != "") cmd_out += reta + " ";
		
		cmd_out += target.getTypeString() + " ";
		
		if (fnty != "") cmd_out += fnty + " ";
		if (fnptrval != "") cmd_out += fnptrval;

		cmd_out += "(";
		if (operands.size() > 0){
			for (int i = 0; i < operands.size(); i++){
				cmd_out += operands.get(i).getTypeString() + " " + operands.get(i).getName();
				if (i+1 < operands.size()) cmd_out += ", "; 
			}
		}
		cmd_out += ") ";
		
		if (fnattrs != "") cmd_out += fnattrs + " ";
		
		cmd_out += " " + getComment();
		
		return cmd_out;
	}
}