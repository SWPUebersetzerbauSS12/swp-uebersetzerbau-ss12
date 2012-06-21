package de.fuberlin.projectcii.ParserGenerator.src;
import java.io.IOException;


import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.projectcii.ParserGenerator.src.extern.lexer.Lexer;
import de.fuberlin.projectcii.ParserGenerator.src.extern.lexer.io.StringCharStream;
import de.fuberlin.projectcii.ParserGenerator.src.extern.utils.IOUtils;

public class Main {
	
	public static void main(String[] args) {
		
		String data;
		try {
			data = IOUtils.readFile("program.txt");
			ILexer lexer = new Lexer(new StringCharStream(data));
			LL1Parser ll1 = new LL1Parser();
			ll1.parse(lexer,"language.txt");		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
