package main;

import main.model.Token;

public class Translator {
	private StringBuffer sectionData;
	private StringBuffer sectionText;
	
	//private Variabelnverwaltung vars;
	//private Registerverwaltung regs;
	
	public Translator(){
		/* Variablen/Registerverwaltung erstellen (oder werden die übergeben?) */
		sectionData = new StringBuffer().append(".section .data\n");
		sectionText = new StringBuffer().append(".section .text\n");
		sectionText.append(".global main\n");
	}
	
	public void translate(Token t){
		switch (t.getType()) {
		case Definition:
			String name = t.getTarget().substring(1);
			sectionText.append("type ").append(name).append(", @function\n").append(name).append(":\n");
			sectionText.append("\tenter $0, $0\n"); //Durch spätere Optimierung oder durch lookahead auf die nächsten Token könnte hier bereits Stackspeicher reserviert werden, 
			/* TODO: Variablenkontext wechseln, Sonderbehandlung main */
			break;
			
		case DefinitionEnd:
			sectionText.append("\tleave\n");
			sectionText.append("\tret\n");
			break;
			
		case Label:
			sectionText.append("_" + t.getTarget() + ":\n");
			break;

		default:
			break;
		}
	}
	
	public void print(){
		System.out.print(sectionData);
		System.out.print(sectionText);
	}
		
}
