package main;


import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import main.model.Address;
import main.model.RegisterAddress;
import main.model.StackAddress;
import main.model.Variable;

public class VarAdministration {
	
	private static int stackPointer = 0;
	private Hashtable<Variable, List<Address>> addressDescriptor;
	private List<Variable>[] regDescriptor;
	private int numRegister;
	private Hashtable<Variable, Address> homeAddress;
	private Hashtable<String, Variable> variableNames;
	
	@SuppressWarnings("unchecked")
	public VarAdministration(int numRegister) {
		this.numRegister = numRegister;
		addressDescriptor = new Hashtable<Variable, List<Address>>();
		homeAddress = new Hashtable<Variable,Address>();
		regDescriptor = new List[numRegister];
		variableNames = new Hashtable<String, Variable>();
		for (int i = 0; i < numRegister; i++) {
			regDescriptor[i] = new LinkedList<Variable>();
		}
	}
	
	public Address getNewAddress(Variable var) {
		Address addr = new StackAddress(stackPointer);
		stackPointer += var.size();
		return addr;
	}
	
	/*
	 * Add Variable and Generate a new Address for it
	 */
	public void addVariable (Variable var) {
		addVariable(var, getNewAddress(var));
	}
	
	/*
	 * Add Variable with Address
	 */
	public void addVariable (Variable var, Address addr) {
		List<Address> addrList = new LinkedList<Address>();
		addrList.add(addr);
		homeAddress.put(var, addr);
		addressDescriptor.put(var, addrList);
		variableNames.put(var.name(), var);
	}
	
	/*
	 * moves the stackpointer back
	 */
	public void freeAddress(Variable var) {
		stackPointer -= var.size();
		homeAddress.remove(var);
		variableNames.remove(var.name());
	}
	
	/*
	 * Add Variable with several Addresses
	 */
//	public void addVariable (Variable var, List<Address> addrList) {
//		addressDescriptor.put(var, addrList);
//	}
	
	/*
	 * 
	 */
	public List<Variable> getAllVariables() {
		List<Variable> varList = new LinkedList<Variable>();
		for (Variable var : addressDescriptor.keySet()) {
			varList.add(var);
		}
		return varList;
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

	public List<Address> getAddresses(Variable var) {
		return addressDescriptor.get(var);
	}
	
	public Address getHomeAddress(Variable var) {
		return homeAddress.get(var);
	}
	
	public Variable getVariable(String name) {
		return variableNames.get(name);
	}

	public boolean hasVariableWithName(String varName) {
		return variableNames.containsKey(varName);
	}
	
	public Variable getVariableByName(String varName) {
		return variableNames.get(varName);
	}

}
