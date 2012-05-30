package main;

import java.io.FileOutputStream;
import java.util.ArrayList;

import main.model.Token;
import main.model.TokenType;

public class CodeGenerator {

	public static String generateCode(String filename, boolean debug,
			boolean guiFlag) {
		// Lexer, Variablenverwaltung und Übersetzter erstellen
		ArrayList<Token> code = new ArrayList<Token>();
		Lexer lex = new Lexer(filename);
		Translator trans = new Translator();

		// Token durchgehen und übersetzten bis EOF
		GUI gui = new GUI();
		int linecount = 0;
		Token tok;
		// Token einlesen
		while ((tok = lex.getNextToken()).getType() != TokenType.EOF) {
			code.add(tok);
		}
		lex.close();

		// Token informationen ausgeben
		if (debug) {
			for (Token t : code) {
				System.out.println("Input file: " + filename);
				System.out.println("File " + filename + " Token #"
						+ linecount++);
				t.print();
			}
		}

		// Token Tabelle in der gui füllen
		if (guiFlag)
			gui.updateTokenStreamTable(code);

		// Token übersetzen
		try {
			trans.translate(code);
		} catch (Exception e) {
			e.printStackTrace();
			if (guiFlag) {
				gui.updateCodeArea(trans.getCode());
				gui.appendCodeArea("\nError:\n");
				for (StackTraceElement errStack : e.getStackTrace())
					gui.appendCodeArea("at " + errStack.getMethodName() + "("
							+ errStack.getFileName() + ":"
							+ errStack.getLineNumber() + ")");
			}
		}
		// Ausgabe des erzeugten Code's
		if (debug) {
			trans.print();
		}

		// Ausgabe des erzeugten Code's in die GUI
		if (guiFlag)
			gui.updateCodeArea(trans.getCode());

		// Rückgabe des erzeugten Code's
		return trans.getCode();
	}

	public static void main(String[] args) {
		boolean debug = true;
		boolean gui = true;

		ArrayList<String> inputFile = new ArrayList<String>();
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
			String output = generateCode(file, debug, gui);
			if (outputFile != null) {
				try{
				    FileOutputStream schreibeStrom = new FileOutputStream(outputFile);
				    for (int i=0; i < output.length(); i++){
				      schreibeStrom.write((byte)output.charAt(i));
				    }
				    schreibeStrom.close();
				}catch(Exception e) {
					
				}
				}
			}
		}
	}
