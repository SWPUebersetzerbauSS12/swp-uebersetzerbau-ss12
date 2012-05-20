package main;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.model.Token;
import main.model.TokenType;
import main.model.Variable;


public class CodeGenerator {

	
	public static void main(String[] args) {
		
		Map<Integer,String> inputFile = new HashMap<Integer,String>();
		String outputFile = "";
		
		System.out.println("Anzahl Parameter: " + args.length);
		
		//Argumente parsen
		for(int i = 0 ; i < args.length ; i++) {
			
			if(args[i].compareTo("-o") == 0) {
				if((i+1) <= args.length)
					outputFile = args[++i];
				else {
					System.out.println("Option -o needs a second parameter");
					return;
				}
			}
			else
				inputFile.put(new Integer(inputFile.size()), args[i]);
		}
		
		//Argumente Fehlerbehandlung
		if(inputFile.size() == 0) {
			System.out.println("No inputfile spezified!");
			return;
		}
		
		System.out.println("Outputfile: " + outputFile);
		for(int i = 0; i < inputFile.size(); i++) {
			System.out.println("Inputfile #" + i + ": " + inputFile.get((Integer)i));
		}
		
		
		
		// Start der Übersetzung
		for(int i = 0; i < inputFile.size(); i++) {
			
			//Lexer erstellen
			Lexer lex = new Lexer();
			//Eingabe Datei parsen
			lex.open(inputFile.get((Integer)i));
			
			VariableTableContainer varCon = new VariableTableContainer();
			Translator trans = new Translator();
			
			Token tok;
			int linecount = 0;
			
			//hole immer neuen Token bis Token mit dem Type EOF kommt
			while((tok = lex.getNextToken()).getType() != TokenType.EOF) {
				System.out.println("File #" + i + " Token #" + linecount++);
				
				printToken(tok);
				
				varCon.updateVarAdministration(tok);
				System.out.println();
				
				trans.translate(tok);
			}
			
			System.out.println("\nGenerated Code:");
			trans.print();
			System.out.println();
			
			lex.close();
			
			//Test ob die Variablen-Tabellen funktionieren
			/*Variable testVar1 = new Variable("Variable1", "i32", "8");
			Variable testVar2 = new Variable("Variable2", "double", "0.5");
			
			Variable globalVar1 = new Variable("Variable1", "i32", "20");
			Variable globalVar2 = new Variable("Variable2", "double", "4.4");
			
			varCon.addVariableTable("test");
			varCon.changeVariableTable("test");
			varCon.addVariable(testVar1);
			varCon.addVariable(testVar2);
			
			varCon.changeVariableTable("global");
			varCon.addVariable(globalVar1);
			varCon.addVariable(globalVar2);
			
			varCon.changeVariableTable("test");
			List<Variable> varList;
			varList = varCon.getAllVariables();
			for (Variable v : varList) {
				System.out.println("name " + v.name() +
								   " of type " + v.type());
			}
//			System.out.println("Variable 1: " + varCon.getVariable("Variable1").value());
//			System.out.println("Variable 2: " + varCon.getVariable("Variable2").value());
			
			varCon.changeVariableTable("global");
			varList = varCon.getAllVariables();
			for (Variable v : varList) {
				System.out.println("name " + v.name() +
								   " of type " + v.type());
			}
//			System.out.println("Variable 1: " + varCon.getVariable("Variable1").value());
//			System.out.println("Variable 2: " + varCon.getVariable("Variable2").value());
		*/	
		}
		
	}
	
	public static void printToken(Token tok) {
		//Ausgabe Type des Tokens
		if(tok.getType() != null)
			System.out.println("\tType: " + tok.getType().toString());
		
		//Ausgabe Target der LLVM Zeile
		if(tok.getTarget() != null) {
			System.out.print("\tTarget: " + tok.getTarget());
			if(tok.getTypeTarget() != null)
				System.out.print(" (" + tok.getTypeTarget() + ")");
			System.out.println();
		}

		//Ausgabe des Operanden1 der LLVM Zeile
		if(tok.getOp1() != null) {
			System.out.print("\tOp1: " + tok.getOp1());
			if(tok.getTypeOp1() != null)
				System.out.print(" (" + tok.getTypeOp1() + ")");
			System.out.println();
		}
		
		//Ausgabe des Operanden2 der LLVM Zeile
		if(tok.getOp2() != null) {
			System.out.print("\tOp2: " + tok.getOp2());
			if(tok.getTypeOp2() != null)
				System.out.print(" (" + tok.getTypeOp2() + ")");
			System.out.println();
		}
		for(int j = 0; j < tok.getParameterCount(); j++) {
			System.out.print("\tParameter: " + tok.getParameter(j).getOperand());
			System.out.println(" (" + tok.getParameter(j).getType() + ")");
		}
		System.out.println();
		
	}

}
