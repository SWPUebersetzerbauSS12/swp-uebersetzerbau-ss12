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

public class StringLexer extends Lexer{

	FileInputStream fstream;
	DataInputStream in;
	BufferedReader br;
	
	String[] code;
	int cursor;

	public StringLexer(String code) {
		this.code = code.split("\n");
		this.cursor = 0;
	}

	public int close() {
		return 0;
	}

	public Token getNextToken() {
		Token token;
		while (cursor < code.length) {
			String[] splitInformation = splitInformation(code[cursor]);
			if (splitInformation.length == 0) {
				cursor++;
				continue;
			} else {				
				token = fillToken(splitInformation);
				cursor++;
				return token;
			}
		}
		token = new Token();
		token.setType(TokenType.EOF);
		return token;
	}
}