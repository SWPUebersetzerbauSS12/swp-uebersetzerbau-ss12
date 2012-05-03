package de.fuberlin.optimierung;

class LLVMBlock implements ILLVMBlock{
	
	private LLVMCommand firstCommand = null;
	private LLVMCommand lastCommand = null;
	private String label = "";
	
	private String blockCode;

	public LLVMBlock(String blockCode) {
	
		this.blockCode = blockCode;
		this.createCommands();

	}

	private void createCommands() {

		String commandsArray[] = this.blockCode.split("\n");
		this.firstCommand = new LLVMCommand(commandsArray[0],null);
		LLVMCommand predecessor = this.firstCommand;
		for(int i=1; i<commandsArray.length; i++) {
			LLVMCommand c = new LLVMCommand(commandsArray[i],predecessor);
			predecessor = c;
		}
		this.lastCommand = predecessor;

	}
		

	/*public void addCmdLine(String cmd){
		
		// Erstelle Befehl
		LLVMCommand cmdLine = new LLVMCommand(cmd, null, null);
		
		// Teste, ob Block beginnt oder endet
		if(this.firstCommand == null) {
			// Fuege ersten Befehl ein
			return;
		}
		// if(Command ist sprung) {
		//		letzter Befehl
		// }
		
		// Weder erster noch letzter Befehl des Blocks
		
	}*/

}
