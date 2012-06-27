package de.fuberlin.projectcii.ParserGenerator.src;
import java.io.IOException;

import de.fuberlin.commons.lexer.ILexer;

import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.io.StringCharStream;
import de.fuberlin.projecta.utils.IOUtils;

public class Main {
	
	public static void main(String[] args) {
		
		String data;
		try {
			data = IOUtils.readFile("program.txt");
			ILexer lexer = new Lexer(new StringCharStream(data));
			LL1Parser ll1 = new LL1Parser();
			ll1.parse(lexer,"language_mod.txt");		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
