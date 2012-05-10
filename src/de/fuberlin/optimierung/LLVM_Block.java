package de.fuberlin.optimierung;

import java.util.LinkedList;

import de.fuberlin.optimierung.commands.*;

class LLVM_Block implements ILLVM_Block {
	
	// Erster und letzter Befehl des Blockes
	private ILLVM_Command firstCommand = null;
	private ILLVM_Command lastCommand = null;

	// Ursprüngliches Label des Blockes
	private String label = "";

	// Vorgaenger- und Nachfolgerbloecke
	// Hieraus entsteht der Flussgraph zwischen den Bloecken
	private LinkedList<ILLVM_Block> nextBlocks = new LinkedList<ILLVM_Block>();
	private LinkedList<ILLVM_Block> previousBlocks = new LinkedList<ILLVM_Block>();
	
	// Kompletter Code des Blocks als String
	private String blockCode;

	public LLVM_Block(String blockCode) {
		
		this.blockCode = blockCode;
		System.out.println(blockCode + "\n*****************\n");
		
		this.createCommands();

	}
	
	public boolean isEmpty() {
		return (this.firstCommand==null);
	}

	public void optimizeBlock() {

	}

	public void deleteBlock() {

	}

	private void createDAG() {

	}

	private void createCommands() {
		String commandsArray[] = this.blockCode.split("\n");
		this.firstCommand = mapCommands(commandsArray[1], null);
		
		ILLVM_Command predecessor = firstCommand;
		for(int i=2; i<commandsArray.length; i++) {
			ILLVM_Command c = mapCommands(commandsArray[i], predecessor);
			if(firstCommand == null){
				firstCommand = c;
				predecessor = c;
			}else{
				predecessor = c;
			}
		}
		this.lastCommand = predecessor;
	}
	
	// Ermittelt Operation und erzeugt Command mit passender Klasse
	//TODO elegante Methode finden, switch funktioniert auf Strings nicht!
	private LLVM_GenericCommand mapCommands(String cmdLine, ILLVM_Command predecessor){
		
		// Kommentar Handling
		String[] com = cmdLine.trim().split(";");
		String comment = "";
		
		if (com.length > 1){
			for (int i = 1; i < com.length; i++){
				comment += com[i]; 
			}
		}
		
		if (com.length == 0) return null;
		
		// Kommando Handling
		String[] cmd = com[0].trim().split("[ \t]");
		
		if (cmd.length > 0){
			if (cmd[0].compareTo("br") == 0){
				if (cmd[1].compareTo("label") == 0){
					return new LLVM_BranchCommand(cmd, LLVM_Operation.BR, predecessor, this, comment);
				}else{
					return new LLVM_BranchCommand(cmd, LLVM_Operation.BR_CON, predecessor, this, comment);
				}
			} else if (cmd[0].compareTo("ret") == 0){
				if (cmd[1].compareTo("void") == 0){
					return new LLVM_ReturnCommand(cmd, LLVM_Operation.RET, predecessor, this, comment);
				}else{
					return new LLVM_ReturnCommand(cmd, LLVM_Operation.RET_CODE, predecessor, this, comment);
				}
			}
			if (cmd.length > 3 && cmd[1].equals("=")){
				
				if (cmd[2].compareTo("add") == 0){
					return new LLVM_ArithmeticCommand(cmd, LLVM_Operation.ADD, predecessor, this, comment);
				}else if(cmd[2].compareTo("sub") == 0){
					return new LLVM_ArithmeticCommand(cmd, LLVM_Operation.SUB, predecessor, this, comment);
				}else if(cmd[2].compareTo("mul") == 0){
					return new LLVM_ArithmeticCommand(cmd, LLVM_Operation.MUL, predecessor, this, comment);
				}else if(cmd[2].compareTo("div") == 0){
					return new LLVM_ArithmeticCommand(cmd, LLVM_Operation.DIV, predecessor, this, comment);
				}else if(cmd[2].compareTo("urem") == 0){
					return new LLVM_ArithmeticCommand(cmd, LLVM_Operation.UREM, predecessor, this, comment);
				}else if(cmd[2].compareTo("srem") == 0){
					return new LLVM_ArithmeticCommand(cmd, LLVM_Operation.SREM, predecessor, this, comment);
				}else if (cmd[2].compareTo("alloca") == 0){
					return new LLVM_Alloca(cmd, LLVM_Operation.ALLOCA, predecessor, this, comment);
				}else if (cmd[2].compareTo("and") == 0){
					return new LLVM_Alloca(cmd, LLVM_Operation.AND, predecessor, this, comment);
				}else if (cmd[2].compareTo("or") == 0){
					return new LLVM_Alloca(cmd, LLVM_Operation.OR, predecessor, this, comment);
				}else if (cmd[2].compareTo("xor") == 0){
					return new LLVM_Alloca(cmd, LLVM_Operation.XOR, predecessor, this, comment);
				}else if (cmd[2].compareTo("icmp") == 0){
					if (cmd[3].compareTo("eq") == 0){
						return new LLVM_IcmpCommand(cmd, LLVM_Operation.ICMP_EQ, predecessor, this, comment);
					}else if (cmd[3].compareTo("ne") == 0){
						return new LLVM_IcmpCommand(cmd, LLVM_Operation.ICMP_NE, predecessor, this, comment);
					}else if (cmd[3].compareTo("ugt") == 0){
						return new LLVM_IcmpCommand(cmd, LLVM_Operation.ICMP_UGT, predecessor, this, comment);
					}else if (cmd[3].compareTo("uge") == 0){
						return new LLVM_IcmpCommand(cmd, LLVM_Operation.ICMP_UGE, predecessor, this, comment);
					}else if (cmd[3].compareTo("ult") == 0){
						return new LLVM_IcmpCommand(cmd, LLVM_Operation.ICMP_ULT, predecessor, this, comment);
					}else if (cmd[3].compareTo("ule") == 0){
						return new LLVM_IcmpCommand(cmd, LLVM_Operation.ICMP_ULE, predecessor, this, comment);
					}else if (cmd[3].compareTo("sgt") == 0){
						return new LLVM_IcmpCommand(cmd, LLVM_Operation.ICMP_SGT, predecessor, this, comment);
					}else if (cmd[3].compareTo("sge") == 0){
						return new LLVM_IcmpCommand(cmd, LLVM_Operation.ICMP_SGE, predecessor, this, comment);
					}else if (cmd[3].compareTo("slt") == 0){
						return new LLVM_IcmpCommand(cmd, LLVM_Operation.ICMP_SLT, predecessor, this, comment);
					}else if (cmd[3].compareTo("sle") == 0){
						return new LLVM_IcmpCommand(cmd, LLVM_Operation.ICMP_SLE, predecessor, this, comment);
					}
				}
			}
		}
		return null;
	}

	public void setFirstCommand(ILLVM_Command first) {
		this.firstCommand = first;
	}

	public void setLastCommand(ILLVM_Command last) {
		this.lastCommand = last;
	}
	
	public ILLVM_Command getFirstCommand() {
		return firstCommand;
	}

	public ILLVM_Command getLastCommand() {
		return lastCommand;
	}
	
	public LinkedList<ILLVM_Block> getNextBlocks() {
		return nextBlocks;
	}

	public void setNextBlocks(LinkedList<ILLVM_Block> nextBlocks) {
		this.nextBlocks = nextBlocks;
	}

	public LinkedList<ILLVM_Block> getPreviousBlocks() {
		return previousBlocks;
	}

	public void setPreviousBlocks(LinkedList<ILLVM_Block> previousBlocks) {
		this.previousBlocks = previousBlocks;
	}
	
	public String toString() {
		
		String code = "";
		
		if(!label.equals("")){
			code = label+":\n";
		}
		
		ILLVM_Command tmp = firstCommand;
		while(tmp != null){
			code += "\t"+tmp.toString();
			tmp = tmp.getSuccessor();
		}
		//code += "\n";
		
		return code;
	}
}
