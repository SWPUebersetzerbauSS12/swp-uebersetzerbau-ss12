package src.main;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.main.model.Address;
import src.main.model.Variable;

public class VariableTableContainer {
	static int numRegister = 4;
	Map<String,VarAdministration> variableTableList;
	String actTable = "";
	
	public VariableTableContainer() {
		variableTableList = new HashMap<String,VarAdministration>();
		
		addVariableTable("global");
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
	
}
