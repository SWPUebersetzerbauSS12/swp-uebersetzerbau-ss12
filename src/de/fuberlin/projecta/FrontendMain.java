package de.fuberlin.projecta;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SemanticAnalyzer;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.io.FileCharStream;
import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.lexer.io.StringCharStream;
import de.fuberlin.projecta.parser.ParseException;
import de.fuberlin.projecta.parser.Parser;
import de.fuberlin.projecta.utils.IOUtils;
import de.fuberlin.projecta.utils.StringUtils;

public class FrontendMain {

	static void run(ICharStream stream) {
		ILexer lexer = new Lexer(stream);
		Parser parser = new Parser();
		try {
			parser.parse(lexer, "");
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println(e.getDetails());
			System.err.println("Parser failed.");
			return;
		}
		
		ISyntaxTree tree = parser.getParseTree();
		//parser.printParseTree();
		
		SemanticAnalyzer analyzer = new SemanticAnalyzer(tree);
		analyzer.analyze();
		try{
			analyzer.getAST().checkSemantics();
			System.out.println("Semantics should be correct");
		} catch (SemanticException e){
			System.out.println("Bad Semantics");
			System.out.println(e.getMessage());
		}
		analyzer.getAST().printTree();
		System.out.println(analyzer.getAST().genCode());
		
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
