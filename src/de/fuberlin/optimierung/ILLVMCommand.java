package de.fuberlin.optimierung;

interface ILLVMCommand {
	
	public void deleteCommand();
	public String getCmd();
	public int getParamsCount();
	public void changeParam(int position, String param);
}
