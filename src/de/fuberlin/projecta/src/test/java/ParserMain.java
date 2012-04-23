import java.io.File;

import lexer.ILexer;
import lexer.Lexer;
import lexer.io.FileCharStream;
import lexer.io.ICharStream;
import lexer.io.StringCharStream;
import parser.Parser;
import parser.ParserException;
import utils.IOUtils;

public class ParserMain {

	static void readStdin() {
		String data = IOUtils.readMultilineStringFromStdin();
		parse(new StringCharStream(data));
	}

	static void readFile(String path) {
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

		parse(new FileCharStream(path));
	}

	static void parse(ICharStream stream) {
		ILexer lexer = new Lexer(stream);
		Parser parser = new Parser(lexer);
		try {
			parser.parse();
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Reading from stdin. Exit with new line and Ctrl+D.");
			readStdin();
		} else if (args.length == 1) {
			final String path = args[0];
			readFile(path);
		} else {
			System.out.println("Wrong number of parameters.");
		}

	}
}
