package main;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.model.Address;
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
	
	public List<Address> getAddresses(Variable var) {
		return getVariableTable(this.actTable).getAddresses(var);
	}
	
	public void updateVarAdministration(Token tok) {
		switch(tok.getType()) {
			case Definition:
				addVariableTable(tok.getTarget());
				System.out.println("\tAction: add variabletable " + tok.getTarget());
				changeVariableTable(tok.getTarget());
				System.out.println("\tAction: change to variabletable " + actTable);
				break;
							
			case DefinitionEnd:
				changeVariableTable("global");
				System.out.println("\tAction: change to variabletable " + actTable);
				break;
				
			case String:
				addVariable(new Variable(tok.getTarget(),tok.getTypeTarget(),tok.getOp1()));
				
				System.out.println("\tAction: add variable " 	+ tok.getTarget() + "\n\t\t"
															+ tok.getTypeTarget() + "\n\t\t"
															+ tok.getOp1());
				System.out.println("\t\tto variabletable: " + actTable);
				break;
							
		}
	}
	
}
