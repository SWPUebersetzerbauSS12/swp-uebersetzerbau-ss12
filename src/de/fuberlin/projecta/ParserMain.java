package de.fuberlin.projecta;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.io.FileCharStream;
import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.parser.ParseException;
import de.fuberlin.projecta.parser.Parser;
import de.fuberlin.projecta.utils.StringUtils;

public class ParserMain {

	static Parser parse(ICharStream stream) {
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
			System.out.println("Details:");
			System.out.println(e.getDetails());
			System.out.println("Trace:");
			e.printStackTrace();
			return null;
		}

		parser.getParseTree().printTree();
		return parser;
	}
	
	private static void run(ICharStream stream) {
		Parser parser = parse(stream);
		if (parser == null)
			return;

		parser.getParseTree().printTree();
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Reading from stdin. Exit with new line and Ctrl+D.");
			ICharStream stream = StringUtils.readFromStdin();
			run(stream);
		} else if (args.length == 1) {
			final String path = args[0];
			FileCharStream stream = StringUtils.readFromFile(path);
			run(stream);
		} else {
			System.out.println("Wrong number of parameters.");
		}
	}
}
