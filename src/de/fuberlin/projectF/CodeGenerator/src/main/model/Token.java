package main.model;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Peter Hirschfeld
 * Das ist ein Token. Der Aufbau erklärt den Token von selbst.
 *
 */

public class Token {
		protected String type;		//Typ des Tokens
		protected String target;	//Ziel aus der 3Adress-Code Zeile
		protected String typeTarget;//Variablentyp des Ziels
		protected String op1;		//Operand1 aus der 3Adress-Code Zeile
		protected String typeOp1;	//Variablentyp des Operand1
		protected String op2;		//Operand2 aus der 3Adress-Code Zeile
		protected String typeOp2;	//Variablentyp des Operand2
		protected Map<Integer, Parameter> parameterList;	//Argumentenliste bei Methodendefinitionen. Da kann es viel in einer Zeile geben
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
		
		public String getTarget() {
			return target;
		}
		
		public void setTarget(String target) {
			this.target = target;
		}
		
		public String getTypeTarget() {
			return typeTarget;
		}
		
		public void setTypeTarget(String typeTarget) {
			this.typeTarget = typeTarget;
		}
		
		public String getOp1() {
			return op1;
		}
		
		public void setOp1(String op1) {
			this.op1 = op1;
		}
		
		public String getTypeOp1() {
			return typeOp1;
		}
		
		public void setTypeOp1(String typeOp1) {
			this.typeOp1 = typeOp1;
		}
		
		public String getOp2() {
			return op2;
		}
		
		public void setOp2(String op2) {
			this.op2 = op2;
		}
		
		public String getTypeOp2() {
			return typeOp2;
		}
		
		public void setTypeOp2(String typeOp2) {
			this.typeOp2 = typeOp2;
		}
		
		public void addParameter(String operand, String type) {
			
			if(parameterList == null)
				parameterList = new HashMap<Integer, Parameter>();
			
			Parameter parameter = new Parameter();
			parameter.setOperand(operand);
			parameter.setType(type);
			
			parameterList.put(new Integer(parameterList.size()), parameter);
		}
		
		public Parameter getParameter(int count) {
			if(parameterList == null)
				return null;
			
			return parameterList.get(new Integer(count));
		}
		
		public int getParameterCount() {
			if(parameterList == null)
				return 0;
			return parameterList.size();
		}
		
		public class Parameter {
			String operand;
			String type;
			
			public String getOperand() {
				return operand;
			}
			
			public void setOperand(String operand) {
				this.operand = operand;
			}
			
			public String getType() {
				return type;
			}
			
			public void setType(String type) {
				this.type = type;
			}	
		}
	}