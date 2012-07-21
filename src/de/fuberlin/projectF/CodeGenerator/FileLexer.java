package de.fuberlin.projectF.CodeGenerator;

import java.io.*;

import de.fuberlin.projectF.CodeGenerator.model.Token;
import de.fuberlin.projectF.CodeGenerator.model.TokenType;

//Diese Funktion 
public class FileLexer extends Lexer{

	FileInputStream fstream;
	DataInputStream in;
	BufferedReader br;
	int linecount;

	public FileLexer(File llvmFile, Debuginfo debug) throws FileNotFoundException{
		super(debug);
		this.open(llvmFile);
		linecount = 0;
	}

	// öffnen der Datei
	public int open(File file) throws FileNotFoundException{
		debug.print("Open input file " + file.toString() + " ...");
		
			fstream = new FileInputStream(file);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));

		debug.println("...[OK]\n");
		return 0;
	}

	public int close() {
		// schließen der Datei
		debug.print("\tClose input file ...");
		try {
			in.close();
		} catch (IOException e) {
			debug.print("\tError while closing input file");
			System.err.println("Error: " + e.getMessage());
		}
		debug.println("...[OK]");
		return 0;
	}
	
	public Token getNextToken() {
		String strLine;
		String[] splitLine;
		

		try {
			// Einlesen der nächsten Zeile
			while ((strLine = br.readLine()) != null) {
				debug.print("\tPreprocessing of llvm-code line #" + ++linecount + " ...");
				splitLine = splitInformation(strLine);
				if (splitLine.length == 0) {
					debug.println("[No relevant information]");
					continue;
				}
				debug.print("...[OK]\t->\t");

				return fillToken(splitLine);
			}
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}

		Token endToken = new Token();
		endToken.setType(TokenType.EOF);
		return endToken;
	}
}