package de.fuberlin.projectF.CodeGenerator;

import java.io.*;

import de.fuberlin.projectF.CodeGenerator.model.Token;
import de.fuberlin.projectF.CodeGenerator.model.TokenType;

/**
 *         Dies ist ein Lexer für einen LLVM Code. Grobe Funktionsweise: Der
 *         LLVM Code wird zeilenweise durchgeparst und es wird pro geparste
 *         Zeile ein Token erzeugt und zurückgegeben.
 * 
 *         Es gibt 3 wesentliche Funktionen: open: öffnet die LLVM-Code Datei
 *         close: schließt die LLVM-Code Datei getNextToken: liesst eine Zeile
 *         der Eingabe Datei und parst diese dann auf relevante Informationen
 *         Diese Informationen werden dann in einem Token gespeichert. Der Token
 *         wird dann zurückgegeben.
 * 
 *         Ist aber noch nicht fertig!!!!!!
 * 
 */

public class FileLexer extends Lexer{

	FileInputStream fstream;
	DataInputStream in;
	BufferedReader br;

	public FileLexer(File llvmFile) {
		this.open(llvmFile);
	}

	// öffnen der Datei
	public int open(File file) {
		try {
			fstream = new FileInputStream(file);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return 0;
	}

	public int close() {
		// schließen der Datei
		try {
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return 0;
	}
	
	public Token getNextToken() {
		String strLine;
		String[] splitLine;

		try {
			// Einlesen der nächsten Zeile
			while ((strLine = br.readLine()) != null) {

				splitLine = splitInformation(strLine);
				if (splitLine.length == 0)
					continue;

				return fillToken(splitLine);
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());

		}

		Token endToken = new Token();
		endToken.setType(TokenType.EOF);
		return endToken;
	}
}