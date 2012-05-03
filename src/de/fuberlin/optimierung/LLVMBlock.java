package de.fuberlin.optimierung;

class LLVMBlock implements ILLVMBlock{
	
	LLVMCommand firstCommand = null;
	LLVMCommand lastCommand = null;
	String label = "";
	
	public void addCmdLine(String cmd){
		
		if(!this.firstCommand) {
			// Fuege ersten Befehl ein
			return;
		}
		// if(Command ist sprung) {
		//		letzter Befehl
		// }
		
		// Weder erster noch letzter Befehl des Blocks
		LLVMCommand cmdLine = new LLVMCommand(cmd);
		commands.add(cmdLine);
	}
}
