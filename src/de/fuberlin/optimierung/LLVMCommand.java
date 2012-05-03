package de.fuberlin.optimierung;

class LLVMCommand implements ILLVMCommand {
	
	private String[] cmd;
	private int paramsCount;
	
	public LLVMCommand(String cmdLine){
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
