package main.model;

public class RegisterInformation {
	public RegisterAddress registerAddress;
	public boolean free;
	public Variable toEvacuate;

	public RegisterInformation(RegisterAddress addr) {
		registerAddress = addr;
		free = true;
		toEvacuate = null;
	}

	public RegisterInformation(RegisterAddress addr, Variable var) {
		registerAddress = addr;
		free = false;
		toEvacuate = var;
	}
}
