package de.fuberlin.projecta;

import java.io.File;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.io.FileCharStream;
import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.lexer.io.StringCharStream;
import de.fuberlin.projecta.parser.ParseException;
import de.fuberlin.projecta.parser.Parser;
import de.fuberlin.projecta.utils.IOUtils;

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
		Parser parser = new Parser();
		try {
			parser.parse(lexer, "");
		} catch (ParseException e) {
			System.out.println(e.getMessage() +
					" (error at line: " + e.getLineNumber() +
					", column: " + e.getOffset() +
					", token: \"" + e.getText() + "\")"
			);
			System.out.println("Details:\n" + e.getDetails());
			System.out.println("Trace:");
			e.printStackTrace();
			return;
		}

		parser.printParseTree();
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
