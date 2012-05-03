package de.fuberlin.optimierung;

class LLVMCommand implements ILLVMCommand {
	
	private LLVMCommand lastCommand = null;
	private LLVMCommand nextCommand = null;
	
	private String[] cmd;
	private int paramsCount;
	
	private boolean isFirstCommand() {
		return (this.lastCommand == null);
	}
	
	private boolean isLastCommand() {
		return (this.nextCommand == null);
	}
	
	public LLVMCommand(String cmdLine, LLVMCommand last, LLVMCommand next){
		this.lastCommand = last;
		this.nextCommand = next;
		cmd = cmdLine.split(" ");
		paramsCount = cmd.length - 1;
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
