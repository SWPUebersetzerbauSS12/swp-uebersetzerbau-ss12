import java.io.File;

import lexer.ILexer;
import lexer.Lexer;
import lexer.io.FileCharStream;
import parser.Parser;
import parser.ParserException;

public class ParserMain {

	public static void main(String[] args) {
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
		
		assert (sourceFile.exists());
		assert (sourceFile.canRead());

		ILexer lex = new Lexer(new FileCharStream(path));
		Parser parser = new Parser(lex);
		try {
			parser.parse();
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
}
