package main.model;

public class StackAddress extends Address {

	private int addr;

	public StackAddress(int addr) {
		this.addr = addr;
	}

	public int getAddress() {
		return addr;
	}

	public String getName() {
		return String.valueOf(addr);
	}

	public String getFullName() {
		return (addr + "(%ebp)");
	}

	public String getType() {
		return "Stack";
	}

}
