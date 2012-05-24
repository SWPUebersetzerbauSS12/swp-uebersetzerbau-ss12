package main;

import java.util.List;

import main.model.Address;
import main.model.StackAddress;
import main.model.Token;
import main.model.Variable;

public class Translator {
	private StringBuffer sectionData;
	private StringBuffer sectionText;
	
	
	private VariableTableContainer vars;
	//private Registerverwaltung regs;
	
	public Translator(VariableTableContainer varCon){
		/* Variablen/Registerverwaltung erstellen (oder werden die übergeben?) */
		vars = varCon;
		
		sectionData = new StringBuffer().append(".section .data\n");
		sectionText = new StringBuffer().append(".section .text\n");
		sectionText.append(".global main\n");
	}
	
	public void translate(Token t){
		Variable target;
		Variable op1;
		Variable op2;
		List<Address> addr;
		List<Address> reg;
		String ziel = "undefined";
		String quelle = "undefined";
		
		switch (t.getType()) {
		case Definition:
			// an dieser Stelle wechseln wir auch den Variablenkontext?
			String name = t.getTarget().substring(1);
			sectionText.append("type ").append(name).append(", @function\n").append(name).append(":\n");
			sectionText.append("\tenter $0, $0\n"); //Durch spätere Optimierung oder durch lookahead auf die nächsten Token könnte hier bereits Stackspeicher reserviert werden, 
			/* TODO: Variablenkontext wechseln, Sonderbehandlung main */
			break;
			
		case DefinitionEnd:
			// hier sollten wir den Varibalenkontext zur�ckgeben
			sectionText.append("\tleave\n");
			sectionText.append("\tret\n");
			break;
			
		case Label:
			sectionText.append("_" + t.getTarget() + ":\n");
			break;

		case Allocation:
			target = vars.getVariable(t.getTarget());
			
			String size = new String("$" + target.size());
			
			sectionText.append("\tsubl " + size + ", %esp \t\t#" + t.getTarget() + "\n");
			break;
		case Assignment:
			
			target = vars.getVariable(t.getTarget());
			addr = vars.getAddresses(target);
			for (Address a : addr) {
				ziel = new String("-" + ((StackAddress)a).getAddress() + "(%bsp)");
				break;
			}
			
			if(t.getOp1().charAt(0) == '%')
				System.out.println("Variable");
			else
				quelle = new String("$" + t.getOp1());
			System.out.println(target.type());
			System.out.println(ziel);
			
			sectionText.append("\tmovl " + quelle + ", " + ziel + "\n");
			
			break;
			
		case Load:
			op1 = vars.getVariable(t.getOp1());
			addr = vars.getAddresses(op1);
			if(op1 != null)
				System.out.println("defined");
			else
				System.out.println("undefined");
			
			for (Address a : addr) {
				quelle = new String("-" + ((StackAddress)a).getAddress() + "(%bsp)");
				break;
			}
			
			ziel = new String("%eax");
			sectionText.append("\tmovl " + quelle + ", " + ziel + " \t\t#load\n");
			break;
			
		case Addition:
			sectionText.append("\tadd %eax, %ebx \t\t#load\n");
		default:
			break;
		}
	}
	
	public void print(){
		System.out.print(sectionData);
		System.out.print(sectionText);
	}
		
}
