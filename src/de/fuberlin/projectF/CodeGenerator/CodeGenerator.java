package de.fuberlin.projectF.CodeGenerator;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import de.fuberlin.projectF.CodeGenerator.model.Token;
import de.fuberlin.projectF.CodeGenerator.model.TokenType;

public class CodeGenerator {

	//Variante f�r File-Input
	public static String generateCode(File llvmFile, String asmType, boolean debug,
			boolean guiFlag) {
		
		Lexer lex = new FileLexer(llvmFile);
		return generateCode2(debug, asmType, guiFlag, lex);
	}
	
	//Variante f�r String-Input
	public static String generateCode(String llvmCode, String asmType, boolean debug,
			boolean guiFlag) {
		Lexer lex = new StringLexer(llvmCode);
		
		return generateCode2(debug, asmType, guiFlag, lex);
	}
	
	//extrahiert weil wir jetzt 2 verschiedene Lexer haben
	private static String generateCode2(boolean debug, String asmType, boolean guiFlag,
			Lexer lex) {
		// Variablenverwaltung und Übersetzter erstellen
		Translator trans = new Translator(asmType);

		// Token durchgehen und übersetzten bis EOF
		GUI gui = guiFlag?new GUI():null;
		int linecount = 0;
		ArrayList<Token> tokenStream;
		// Token einlesen
		
		tokenStream = lex.getTokenStream();
		if(tokenStream == null) {
			System.out.println("Error");
		}
		lex.close();

		// Token informationen ausgeben
		if (debug) {
			for (Token t : tokenStream) {
				
				System.out.println("Token #" + linecount++);
				t.print();
			}
		}

		// Token Tabelle in der gui füllen
		if (guiFlag)
			gui.updateTokenStreamTable(tokenStream);

		// Token übersetzen
		try {
			trans.translate(tokenStream);
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
		boolean exec = false;
		String asmType = "gnu";

		ArrayList<String> inputFile = new ArrayList<String>();
		//Inhalt der inputFiles als String
		ArrayList<String> inputStrings = new ArrayList<String>();
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
			} else if (args[i].compareTo("-e") == 0) {
				exec = true;
			} else if (args[i].compareTo("-intel") == 0) {
				asmType = "intel";
			} else if (args[i].compareTo("-gnu") == 0) {
				asmType = "gnu";
			} else
				inputFile.add(args[i]);
		}

		// Argumente Fehlerbehandlung
		if (inputFile.size() == 0) {
			System.out.println("No inputfile spezified!");
			return;
		}
		

		for (String filename : inputFile) {
			File file = new File(filename);
			String output = generateCode(file, asmType, debug, gui);
			if (outputFile != null) {
				writeFile(exec, outputFile, output);
			}
		}
	
		if (exec == true) {
			exec(outputFile);
		}
	}

	public static void writeFile(boolean exec, String outputFile, String output) {
		try{
			FileOutputStream schreibeStrom;
			if(exec == true)
				schreibeStrom = new FileOutputStream(outputFile + ".s");
			else
				schreibeStrom = new FileOutputStream(outputFile);
		    for (int i=0; i < output.length(); i++){
		      schreibeStrom.write((byte)output.charAt(i));
		    }
		    schreibeStrom.close();
		}catch(Exception e) {
			
		}
	}

	public static void exec(String outputFile) {
		if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
			System.err.println("The -e option is not applicable for windows systems.");
			System.err.println("Please change your operating system and do not support such stuff like windows");
			return;
		} else if(System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0) {
			System.out.println("Yeah LINUX :-)");
			
			String libc, ld_linux;
			//String path = 
			
			try {
				String command = "locate lib32/libc.so";
				System.out.println(command);
				Process process = Runtime.getRuntime().exec(command);
				Reader r = new InputStreamReader(process.getInputStream());
			    BufferedReader in = new BufferedReader(r);
			    if((libc = in.readLine()) == null) {
			    	System.err.println("Couldn't find 32bit libc.so library");
			    	return;
			    }	
			    System.out.println("Found libc: " + libc);
			    
			    command = "locate lib32/ld-linux.so";
			    System.out.println(command);
			    process = Runtime.getRuntime().exec(command);
				r = new InputStreamReader(process.getInputStream());
			    in = new BufferedReader(r);
			    if((ld_linux = in.readLine()) == null) {
			    	System.err.println("Couldn't find 32bit ld-linux.so library");
			    	return;
			    }	
			    System.out.println("Found ld-linux: " + ld_linux);
			    
			    String line;
			    command = "as -32 -o a.out " + outputFile + ".s";
			    System.out.println(command);
			    process = Runtime.getRuntime().exec(command);
				r = new InputStreamReader(process.getInputStream());
			    in = new BufferedReader(r);
			    if((line = in.readLine()) != null) {
			    	do {
			    		System.err.println(line);
			    	}while((line = in.readLine()) != null);
			    	return;
			    }
			    
			    command = new String("ld -melf_i386 --dynamic-linker " + ld_linux + " " + libc + " -o " + outputFile + " a.out");
			    System.out.println(command);
			    process = Runtime.getRuntime().exec(command);
				r = new InputStreamReader(process.getInputStream());
			    in = new BufferedReader(r);
			    if((line = in.readLine()) != null) {
			    	do {
			    		System.err.println(line);
			    	}while((line = in.readLine()) != null);
			    	return;
			    }
			    
			    command = "rm a.out";
			    System.out.println(command);
			    process = Runtime.getRuntime().exec(command);
				r = new InputStreamReader(process.getInputStream());
			    in = new BufferedReader(r);
			    if((line = in.readLine()) != null) {
			    	do {
			    		System.err.println(line);
			    	}while((line = in.readLine()) != null);
			    	return;
			    }
			    
			    
			    
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("unknown oparating system detected");
		}
	}
}
