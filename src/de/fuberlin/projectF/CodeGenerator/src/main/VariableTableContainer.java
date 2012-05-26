package main;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.model.Address;
import main.model.RegisterAddress;
import main.model.RegisterInformation;
import main.model.StackAddress;
import main.model.Token;
import main.model.Variable;

public class VariableTableContainer {
	static int numRegister = 4;
	Map<String,VarAdministration> variableTableList;
	String actTable = "";
	
	public VariableTableContainer() {
		variableTableList = new HashMap<String,VarAdministration>();
		addVariableTable("global");
		System.out.println("Action: add variabletable global");
		changeVariableTable("global");
		System.out.println("Action: change to variabletable " + actTable);
	}
	
	public void addVariableTable(String name) {
		variableTableList.put(name, new VarAdministration(numRegister));
	}
	
	public VarAdministration getVariableTable(String name) {
		if(name == null)
			return variableTableList.get(this.actTable);
		return variableTableList.get(name);
	}
	
	public int changeVariableTable(String name) {
		this.actTable = name;
		return 0;
	}
	
	public void addVariable(Variable variable) {
		getVariableTable(this.actTable).addVariable(variable);
	}
	
	public List<Variable> getAllVariables() {
		return getVariableTable(this.actTable).getAllVariables();
	}
	
	public Variable getVariable(String name) {
		List<Variable> varList;
		varList = getAllVariables();
		for (Variable v : varList) {
			if(name.equals(v.name()))
				return v;
		}
		return null;
	}
	
	public Address getHomeAddress(Variable var) {
		return getVariableTable(this.actTable).getHomeAddress(var);
	}
	
	public List<Address> getAddresses(Variable var) {
		return getVariableTable(this.actTable).getAddresses(var);
	}
	
	public Address getBestAddress(Variable var) {
		//TODO
		return null;
	}
	
	public RegisterInformation getReg(Variable var) {
		return getVariableTable(this.actTable).getReg(var);
	}
	
	public Variable getVarByName(String name) {
		return getVariableTable(this.actTable).getVariableByName(name);
	}
	
	public String updateVarAdministration(Token tok) {
		switch(tok.getType()) {
			case Definition:
				addVariableTable(tok.getTarget());
				System.out.println("\tAction: add variabletable " + tok.getTarget());
				changeVariableTable(tok.getTarget());
				System.out.println("\tAction: change to variabletable " + actTable);
				return null;
							
			case DefinitionEnd:
				changeVariableTable("global");
				System.out.println("\tAction: change to variabletable " + actTable);
				return null;
				
			case String:
				addVariable(new Variable(tok.getTarget(),tok.getTypeTarget(),tok.getOp1()));
				
				System.out.println("\tAction: add variable " 	+ tok.getTarget() + "\n\t\t"
															+ tok.getTypeTarget() + "\n\t\t"
															+ tok.getOp1());
				System.out.println("\t\tto variabletable: " + actTable);
				return null;
			
			case Assignment:
			case Allocation:
				// If the Target of an Assignment is not yet in the
				// Table we add a new variable
				if (!hasVariable(tok.getTarget())) {
					System.out.println("\tCould not find : " + tok.getTarget());
					addVariable(new Variable(tok.getTarget(),tok.getTypeTarget(),null));
					System.out.println("\tAction: add variable " 	+ tok.getTarget() + "\n\t\t"
							+ tok.getTypeTarget());
					System.out.println("\t\tto variabletable: " + actTable);
				}
				return null;
			case Load:
				System.out.println("load!");
				String code = null;
				Variable var = getVariable(tok.getOp1());
				Variable target = new Variable(tok.getTarget(), tok.getTypeOp1(), null);
				RegisterInformation info = getVariableTable(this.actTable).getReg(var);
				RegisterAddress reg = info.registerAddress; 
				if (!info.free) {
					code = "mov %" + reg + " , " + getVariableTable(this.actTable).getHomeAddress(var) + "(%ebp) #Temp-Variable added";
				}
				getVariableTable(this.actTable).loadVarInReg(var, reg);
				getVariableTable(this.actTable).addVariable(target, reg);
				return code;
		}
		return null;
	}

	private boolean hasVariable(String varName) {
		
		return getVariableTable(this.actTable).hasVariableWithName(varName);
	}
	
}
