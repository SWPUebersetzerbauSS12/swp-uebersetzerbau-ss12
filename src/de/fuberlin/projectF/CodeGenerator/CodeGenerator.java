package de.fuberlin.projectF.CodeGenerator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.fuberlin.projectF.CodeGenerator.model.Token;

//Dies ist die Hauptklasse des Projekts und kümmert sich um globale angelegenheiten wie
//die übergebenen Parameter. Sie stellt auch das Interface zur Anbindung an das Hauptprojekt dar.
public class CodeGenerator {

	//Variante f�r File-Input
	public static String generateCode(File llvmFile, String asmType, boolean debug,
			boolean guiFlag) {
		
		Debuginfo debuginfo = new Debuginfo(debug);
		try {
			Lexer lex = new FileLexer(llvmFile, debuginfo);
			return generateCode2(debuginfo, asmType, guiFlag, lex);
		} catch (FileNotFoundException e) {
			System.err.println("Could't find input file " + llvmFile);
			e.printStackTrace();
		}
		return "";
	}
	
	//Variante f�r String-Input
	public static String generateCode(String llvmCode, String asmType, boolean debug,
			boolean guiFlag) {
		
		Debuginfo debuginfo = new Debuginfo(debug);
		Lexer lex = new StringLexer(llvmCode, debuginfo);
		return generateCode2(debuginfo, asmType, guiFlag, lex);
	}
	
	//extrahiert weil wir jetzt 2 verschiedene Lexer haben
	private static String generateCode2(Debuginfo debuginfo, String asmType, boolean guiFlag,
			Lexer lex) {
		// Variablenverwaltung und Übersetzter erstellen
		Translator trans = new Translator(asmType);

		// Token durchgehen und übersetzten bis EOF
		GUI gui = null;
		int linecount = 0;
		ArrayList<Token> tokenStream;
		// Token einlesen
		
		debuginfo.println("---> Start LLVM Code Parser --->\n");
		tokenStream = lex.getTokenStream();
		if(tokenStream == null) {
			System.out.println("Error");
		}
		lex.close();
		debuginfo.println("\n<--- LLVM Code Parser finished <---");

		// Token informationen ausgeben
		debuginfo.println("---> Print out detailed token information --->\n");
		if (debuginfo.getDebugflag()) {
			for (Token t : tokenStream) {
				System.out.println("Token #" + linecount++);
				t.print();
			}
		}
		debuginfo.println("\n<--- Print out Token information end <---");

		// Token Tabelle in der gui füllen
		if (guiFlag) {
			gui = new GUI();
			gui.updateTokenStreamTable(tokenStream);
		}

		// Token übersetzen
		try {
			debuginfo.println("---> Start of translation --->\n");
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
		debuginfo.println("\n<--- Translation finished <---");
						
		// Ausgabe des erzeugten Code's
		debuginfo.println("---> Print out generated assembler code --->\n");
		if (debuginfo.getDebugflag()) {
			trans.print();
		}
		debuginfo.println("\n<--- End of generated code<---");

		// Ausgabe des erzeugten Code's in die GUI
		if (guiFlag)
			gui.updateCodeArea(trans.getCode());

		// Rückgabe des erzeugten Code's
		return trans.getCode();
	}
	

	public static void main(String[] args) {
		boolean debug = false;
		boolean gui = false;
		boolean exec = false;
		String asmType = "gnu";

		ArrayList<String> inputFile = new ArrayList<String>();
		//Inhalt der inputFiles als String
		String outputFile = null;
		String configFile = "mc_config.cfg";

		// Argumente parsen
		for (int i = 0; i < args.length; i++) {

			if (args[i].compareTo("-o") == 0 || args[i].compareTo("--output") == 0) {
				if ((i + 1) <= args.length)
					outputFile = args[++i];
				else {
					System.err.println("Option -o needs a second parameter");
					return;
				}
			} else if (args[i].compareTo("-C") == 0 || args[i].compareTo("--config") == 0) {
				if ((i + 1) <= args.length)
					configFile = args[++i];
				else {
					System.err.println("Option -C needs a second parameter");
					return;
				}
			} else if (args[i].compareTo("-asmType") == 0) {
				if ((i + 1) <= args.length){
					i++;
					if(args[i].compareTo("intel") == 0)
						asmType = "intel";
					else if(args[i].compareTo("gnu") == 0)
						asmType = "gnu";
					else {
						System.err.println("The assembler type " + args[i] + " is not supported");
						return;
					}
				} else {
					System.err.println("Option -asmType needs a second parameter");
					return;
				}
			} else if (args[i].compareTo("-e") == 0 || args[i].compareTo("--exec") == 0) {
				exec = true;
			} else if (args[i].compareTo("-intel") == 0) {
				asmType = "intel";
			} else if (args[i].compareTo("-gnu") == 0) {
				asmType = "gnu";
			} else if (args[i].compareTo("-v") == 0 || args[i].compareTo("--verbose") == 0) {
				debug = true;
			} else if (args[i].compareTo("-g") == 0 || args[i].compareTo("--gui") == 0) {
				gui = true;
			} else
				inputFile.add(args[i]);
		}

		// Argumente Fehlerbehandlung
		if (inputFile.size() == 0) {
			System.err.println("No inputfile spezified!");
			return;
		}
		

		for (String filename : inputFile) {
			File file = new File(filename);
			String output = generateCode(file, asmType, debug, gui);
			if (outputFile != null) {
				writeFile(exec, outputFile, output);
			}
		}
		
		//Wenn -e gesetzt dann erstelle die auszuführende Datei anhand der Befehle
		//aus der mit -C angegebenen Config datei
		if (exec == true) {
			exec(outputFile, configFile);
		}
		
	}

	//Schreiben des erzeugten Assemblercodes in die Ausgabedatei
	public static void writeFile(boolean exec, String outputFile, String output) {
		try{
			FileOutputStream schreibeStrom;
			//Falls -e gesetzt schreibe den Code in eine .asm Datei
			if(exec == true)
				schreibeStrom = new FileOutputStream(outputFile + ".asm");
			//Falls -e nicht gesetzt schreibeCode direkt in die Ausgabedatei
			else
				schreibeStrom = new FileOutputStream(outputFile);
		    for (int i=0; i < output.length(); i++){
		      schreibeStrom.write((byte)output.charAt(i));
		    }
		    schreibeStrom.close();
		    
		}catch(IOException e) {
			System.err.println("Couldn't write output file");
			e.printStackTrace();
		}
	}

	//Diese Funktion liesst die einzelnen Zeilen der Config Datei,
	//ersetzt in den gelesenen Zeilen die Platzhalter <input> und <output>
	//und führt dies dann als Befehle auf der Commandozeile aus
	public static void exec(String outputFile, String configFile) {
		
		//Testen on Windows oder Linux 
		if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0 ||
			System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0) {
			
			String line;
			FileInputStream fstream;
			DataInputStream in;
			BufferedReader br;
			
			try {
				//Lesen der Config Datei Zeilen
				fstream = new FileInputStream(configFile);
				in = new DataInputStream(fstream);
				br = new BufferedReader(new InputStreamReader(in));
				
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (line.length() == 0 || line.charAt(0) == '#') {
						continue;
					}
					
					//ersetzen der Platzhalter
					line = line.replace("<input>", outputFile + ".asm");
					line = line.replace("<output>", outputFile);
					
					//ausführen der gelesenen Zeile
					Runtime.getRuntime().exec(line).waitFor();
				}
				fstream.close();

			//Fehlerbehandlung
			} catch (FileNotFoundException e) {
				System.err.println("Could't find config file");
				e.printStackTrace();
			} catch(IOException e) {
				System.err.println("Failed to read from config file");
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.err.println("Failed to execute command");
				e.printStackTrace();
			}
		} else {
			System.err.println("unknown oparating system detected");
		}
	}
}