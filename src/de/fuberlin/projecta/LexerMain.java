package de.fuberlin.projecta;

import java.io.File;
import java.io.IOException;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.SyntaxErrorException;
import de.fuberlin.projecta.lexer.io.FileCharStream;
import de.fuberlin.projecta.lexer.io.StringCharStream;
import de.fuberlin.projecta.utils.IOUtils;

public class LexerMain {

	private static void readStdin() {
		String data = IOUtils.readMultilineStringFromStdin();
		Lexer lexer = new Lexer(new StringCharStream(data));
		printTokens(lexer);
	}

	private static void readFile(String path) {
		File sourceFile = new File(path);
		if (!sourceFile.exists()) {
			System.out.println("File does not exist.");
			return;
		}

		if (!sourceFile.canRead()) {
			System.out.println("File is not readable");
		}
		Lexer lexer = new Lexer(new FileCharStream(path));
		printTokens(lexer);
	}

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

	/**
	 * First parameter should be the source file
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("Reading from stdin. Exit with new line and Ctrl+D.");
			readStdin();
		} else if (args.length == 1) {
			final String path = args[0];
			readFile(path);
		} else {
			System.out.println("Wrong number of arguments!");
		}
	}

}
