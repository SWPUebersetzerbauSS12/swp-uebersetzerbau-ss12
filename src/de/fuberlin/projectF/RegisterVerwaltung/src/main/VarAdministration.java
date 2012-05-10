package main;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class VarAdministration {
	
	Hashtable<Variable, List<Address>> addressDescriptor;
	List<Variable>[] regDescriptor;
	int numRegister;
	
	@SuppressWarnings("unchecked")
	public VarAdministration(int numRegister) {
		this.numRegister = numRegister;
		addressDescriptor = new Hashtable<Variable, List<Address>>();
		regDescriptor = new List[numRegister];
		for (int i = 0; i < numRegister; i++) {
			regDescriptor[i] = new LinkedList<Variable>();
		}
	}
	
	public void loadVarInReg(Variable var, RegisterAddress reg) {
		/*
		 * reg now only holds var
		 */
		regDescriptor[reg.regNumber] = new LinkedList<Variable>();
		regDescriptor[reg.regNumber].add(var);
		/*
		 * var is now also in reg
		 */
		addressDescriptor.get(var).add(reg);
	}
	
	public void storeRegAtAddress(RegisterAddress reg, Address addr) {
		/*
		 * add addr as memorylocation to every variable in the register
		 */
		List<Variable> varsInReg = regDescriptor[reg.regNumber];
		for (Variable var : varsInReg) {
			addressDescriptor.get(var).add(addr);
		}
	}
	
	public void performOperation(RegisterAddress targetAddr, Variable targetVar,
								 RegisterAddress op1Addr,    Variable op1Var,
								 RegisterAddress op2Addr,    Variable op2Var) {
		/*
		 * targetVar is now targetAddr and nowhere else
		 */
		for (int i = 0; i < numRegister; i++) {
			regDescriptor[i].remove(targetVar);
		}
		regDescriptor[targetAddr.regNumber] = new LinkedList<Variable>();
		regDescriptor[targetAddr.regNumber].add(targetVar);
		
		/*
		 * and only targetAddr contains targetVar
		 */
		List<Address> targetAddrList = new LinkedList<Address>();
		targetAddrList.add(targetAddr);
		addressDescriptor.put(targetVar, targetAddrList);
		
	}
	
	/*
	 * for the statement x = y
	 */
	public void copyStatement(RegisterAddress xAddr, Variable xVar,
							  RegisterAddress yAddr) {
		
		/*
		 * the value of the variable x is now also in the register of y
		 */
		regDescriptor[yAddr.regNumber].add(xVar);
		/*
		 * x is only in the register of y
		 */
		List<Address> xAddrList = new LinkedList<Address>();
		xAddrList.add(yAddr);
		addressDescriptor.put(xVar, xAddrList);
	}

}
