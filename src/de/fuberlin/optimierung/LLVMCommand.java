package de.fuberlin.optimierung;

class LLVMCommand implements ILLVMCommand {
	
	private ILLVMBlock block;

	private ILLVMCommand predecessor = null;
	private ILLVMCommand successor = null;
	
	private String[] cmd;
	private int paramsCount;
	
	private boolean isFirstCommand() {
		return (this.predecessor == null);
	}
	
	private boolean isLastCommand() {
		return (this.successor == null);
	}

	private boolean isEmpty() {
		return (this.isFirstCommand() && this.isLastCommand());
	}
	
	public LLVMCommand(String cmdLine, ILLVMCommand predecessor, ILLVMBlock block){
		// Setze die Zeiger
		this.predecessor = predecessor;
		if(!this.isFirstCommand())
			this.predecessor.setSuccessor(this);		

		// Verarbeite den Befehl
		this.cmd = cmdLine.split(" ");
		this.paramsCount = this.cmd.length - 1;

		// Setze den zugehoerigen Basisblock
		this.block = block;

	}

	public ILLVMCommand getPredecessor() {
		return this.predecessor;
	}

	public ILLVMCommand getSuccessor() {
		return this.successor;
	}

	public void setPredecessor(ILLVMCommand c) {
		this.predecessor = c;
	}

	public void setSuccessor(ILLVMCommand c) {
		this.successor = c;
	}

	public void deleteCommand() {
		
		if(this.isFirstCommand()) {	// Loesche erstes Element
			this.successor.setPredecessor(null);
			this.block.setFirstCommand(this.successor);
		}
		if(this.isLastCommand()) {	// Loesche letztes Element
			this.predecessor.setSuccessor(null);
			this.block.setLastCommand(this.predecessor);
		}
 
		this.predecessor.setSuccessor(this.successor);
		this.successor.setPredecessor(this.predecessor);

	}
	
	public String getCmd(){
		String str = "";
		for(int i = 0; i < paramsCount; i++){
			if(i < paramsCount-1){
				str += cmd[i] + " ";
			}else{
				str += cmd[i];
			}
		}
		return str;
	}
	
	public int getParamsCount(){
		return paramsCount;
	}
	
	public void changeParam(int position, String param){
		if(position <= paramsCount){
			cmd[position] = param;
		}else{
			System.err.println(cmd[0] +" has only "+ paramsCount +" params");
		}
	}
}
