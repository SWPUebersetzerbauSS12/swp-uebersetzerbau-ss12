class LLVMArithmeticOperation implements ILLVMCommand {
	
	private ILLVMBlock block;

	private ILLVMCommand predecessor = null;
	private ILLVMCommand successor = null;
	
	private LLVMOperation operation;
	private LLVMParameter target;
	private LinkedList<LLVMParameter> operands;
	
	private boolean has_nuw = false;
	private boolean has_nsw = false;
	
	public LLVMArithmeticOperation(LLVMOperation operation, String[] cmd, ILLVMCommand predecessor, ILLVMBlock block){
		// Setze die Zeiger
		this.operation = operation;
		this.predecessor = predecessor;
		if(!this.isFirstCommand())
			this.predecessor.setSuccessor(this);
		this.block = block;

		// Init operands
		operands = new LinkedList<LLVMParameter>();

		// Verarbeite Kommando
		String[] cmd_parts = cmd[0].split(" ");
		
		switch(cmd_parts.length){
			case 6 :
				if(cmd_parts[3].equals("nuw") == 0)
					has_nuw = true;
				else
					has_nsw = true;
				target = new LLVMParameter(cmd_parts[0], cmd_parts[4]);
				operands.add(new LLVMParameter(cmd_parts[5], cmd_parts[4]));
				break;
			case 7 :
				has_nuw = true;
				has_nsw = true;
				target = new LLVMParameter(cmd_parts[0], cmd_parts[5]);
				operands.add(new LLVMParameter(cmd_parts[6], cmd_parts[5]));
				break;
			default:
				target = new LLVMParameter(cmd_parts[0], cmd_parts[3]);
				operands.add(new LLVMParameter(cmd_parts[4], cmd_parts[3]));
				break;
		}
		operands.add(new LLVMParameter(cmd[1], cmd_parts[4]));
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
}