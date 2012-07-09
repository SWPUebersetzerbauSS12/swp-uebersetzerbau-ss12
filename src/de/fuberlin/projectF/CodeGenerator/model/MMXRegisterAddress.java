package de.fuberlin.projectF.CodeGenerator.model;

public class MMXRegisterAddress extends Address {

	public int regNumber;

	public MMXRegisterAddress(int register) {
		this.regNumber = register;
	}

	public String getName() {
		return "regNumber";
	}

	public String getFullName() {
		switch (regNumber) {
		case 0:
			return "xmm0";
		case 1:
			return "xmm1";
		case 2:
			return "xmm2";
		case 3:
			return "xmm3";
		case 4:
			return "xmm4";
		case 5:
			return "xmm5";
		case 6:
			return "xmm6";
		default:
			return "xmm7";
		}
	}

	public String getType() {
		return "XMMRegister";
	}

}
