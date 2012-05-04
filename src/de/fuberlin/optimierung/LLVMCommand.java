package de.fuberlin.optimierung;

class LLVMCommand implements ILLVMCommand {
	
	private LLVMCommand predecessor = null;
	private LLVMCommand successor = null;
	
	private String[] cmd;
	private int paramsCount;
	
	private boolean isFirstCommand() {
		return (this.predecessor == null);
	}
	
	private boolean isLastCommand() {
		return (this.successor == null);
	}
	
	public LLVMCommand(String cmdLine, LLVMCommand predecessor){
		// Setze die Zeiger
		this.predecessor = predecessor;
		if(!this.isFirstCommand())
			this.predecessor.successor = this;		

		// Verarbeite den Befehl
		this.cmd = cmdLine.split(" ");
		this.paramsCount = this.cmd.length - 1;
	}

	public void deleteCommand() {

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
