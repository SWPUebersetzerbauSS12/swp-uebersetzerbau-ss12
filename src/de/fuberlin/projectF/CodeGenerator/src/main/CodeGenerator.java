package main;

import java.util.LinkedList;
import main.model.Token;
import main.model.TokenType;

public class CodeGenerator {

	public static String generateCode(String filename, boolean debug) {
		// Lexer, Variablenverwaltung und Übersetzter erstellen
		Lexer lex = new Lexer(filename);
		VariableTableContainer varCon = new VariableTableContainer();
		Translator trans = new Translator(varCon);
		
		// Token durchgehen und übersetzten bis EOF
		Token tok;
		int linecount = 0;
		while ((tok = lex.getNextToken()).getType() != TokenType.EOF) {
			if (debug) {
				System.out.println("Input file: " + filename);
				System.out.println("File " + filename + " Token #"
						+ linecount++);
				tok.print();
			}
			
			String code = varCon.updateVarAdministration(tok);
			if (code != null) {
				trans.addCode(code);				
			}
			trans.translate(tok);
		}
		lex.close();
		if (debug) {
			trans.print();
		}
		return trans.getCode();
	}

	public static void main(String[] args) {
		boolean debug = true;

		LinkedList<String> inputFile = new LinkedList<String>();
		String outputFile = null;

		// Argumente parsen
		for (int i = 0; i < args.length; i++) {

			if (args[i].compareTo("-o") == 0) {
				if ((i + 1) <= args.length)
					outputFile = args[++i];
				else {
					System.out.println("Option -o needs a second parameter");
					return;
				}
			} else
				inputFile.add(args[i]);
		}

		// Argumente Fehlerbehandlung
		if (inputFile.size() == 0) {
			System.out.println("No inputfile spezified!");
			return;
		}

		for (String file : inputFile) {
			String code = generateCode(file, debug);
			if (outputFile != null) {
				// TODO: Ausgabe in Datei
			}
		}
	}
}
