package de.fuberlin.projecta;

import java.io.IOException;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.SyntaxErrorException;
import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.utils.StringUtils;

public class LexerMain {

	private static void printTokens(ILexer lexer) {
		IToken t;
		try {
			do {
				t = lexer.getNextToken();
				if(t.getAttribute() != null)
				System.out.println(t.getAttribute().getClass().toString());
				System.out.println(t);
				
			} while (!t.getType().equals("EOF"));
		} catch (SyntaxErrorException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void run(ICharStream stream) {
		Lexer lexer = new Lexer(stream);
		printTokens(lexer);
	}

	/**
	 * First parameter should be the source file
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("Reading from stdin. Exit with new line and Ctrl+D.");
			ICharStream stream = StringUtils.readFromStdin();
			run(stream);
		} else if (args.length == 1) {
			final String path = args[0];
			ICharStream stream = StringUtils.readFromFile(path);
			run(stream);
		} else {
			System.out.println("Wrong number of arguments!");
		}
	}

}
