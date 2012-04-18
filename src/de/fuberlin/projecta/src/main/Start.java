package main;

import java.io.File;
import java.io.IOException;

import lexer.*;

public class Start {

	/**
	 * First parameter should be the source file
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		if(args.length != 1){
//			System.out.println("Wrong number of parameters.");
//			return;
//		}
//		String path = args[0];
		String path = "/home/christian/Desktop/quell.txt";
		File sourceFile = new File(path);
		if(!sourceFile.exists()){
			System.out.println("File does not exist.");
			return;
		}
		
		if(!sourceFile.canRead()){
			System.out.println("File is not readable");
		}
		Lexer lex = new Lexer(sourceFile);
		Token t;
		while((t = lex.nextToken()) != null){
			System.out.println("<" + t.toString() + "> ");
		}
	}

}
