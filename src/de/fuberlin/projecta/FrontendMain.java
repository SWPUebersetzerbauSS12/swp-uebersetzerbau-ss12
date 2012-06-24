package de.fuberlin.projecta;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SemanticAnalyzer;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.lexer.io.FileCharStream;
import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.parser.Parser;
import de.fuberlin.projecta.utils.StringUtils;

public class FrontendMain {

	static String genCode(ICharStream stream) {
		Parser parser = ParserMain.parse(stream);
		if (parser == null) {
			System.err.println("Parsing failed.");
			return null;
		}

		ISyntaxTree tree = parser.getParseTree();
		//parser.printParseTree();

		SemanticAnalyzer analyzer = new SemanticAnalyzer(tree);
		analyzer.analyze();
		try {
			analyzer.getAST().checkSemantics();
			System.out.println("Semantics should be correct");
		} catch (SemanticException e) {
			System.out.println("Bad Semantics");
			System.out.println(e.getMessage());
			return null;
		}

		analyzer.getAST().printTree();
		return analyzer.getAST().genCode();
	}

	private static void run(ICharStream stream) {
		final String code = genCode(stream);
		System.out.println("Generated code:");
		System.out.flush();
		System.out.println(code);
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
