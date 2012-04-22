import lexer.ILexer;
import lexer.Lexer;
import lexer.SyntaxErrorException;
import lexer.Token;

import java.io.File;
import java.io.IOException;

public class LexerMain {

	/**
	 * First parameter should be the source file
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Wrong number of parameters.");
			return;
		}
		final String path = args[0];

		File sourceFile = new File(path);
		if (!sourceFile.exists()) {
			System.out.println("File does not exist.");
			return;
		}

		if (!sourceFile.canRead()) {
			System.out.println("File is not readable");
		}
		ILexer lex = new Lexer(path);
		Token t;
		try {
			while ((t = lex.getNextToken()) != null) {
				System.out.println(t);
			}
		} catch (SyntaxErrorException e) {
			System.out.println(e.getMessage());
		}
	}

}
