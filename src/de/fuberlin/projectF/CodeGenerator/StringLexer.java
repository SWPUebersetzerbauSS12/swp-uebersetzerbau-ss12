package de.fuberlin.projectF.CodeGenerator;

import java.io.*;

import de.fuberlin.projectF.CodeGenerator.model.Token;
import de.fuberlin.projectF.CodeGenerator.model.TokenType;

//Die Klasse StringLexer ist zuständig für das Parsen eines Strings in dem der
//zu übersetzende LLVM Code enthalten ist. Sie erbt alle Funktionen vom Lexer
public class StringLexer extends Lexer{

	FileInputStream fstream;
	DataInputStream in;
	BufferedReader br;
	
	String[] code;
	int cursor;

	public StringLexer(String code, Debuginfo debug) {
		super(debug);
		this.code = code.split("\n");
		this.cursor = 0;
	}

	public int close() {
		return 0;
	}

	//geht durch alle LLVM Zeilen und erzeugt die Token
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