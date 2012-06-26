package de.fuberlin.projectci.test.parser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SemanticAnalyzer;
import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.io.StringCharStream;
import de.fuberlin.projectci.lrparser.LRParser;

public class LRParserTest {
	
	
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testParse() {
		
		String grammarPath="./input/de/fuberlin/projectci/non-ambigous.txt";
		String strProgram=
				"def int fib(int n){\n"+
				"  if (n <= 1) return n;\n"+
				"  else {\n"+
				"    int fib;\n"+
				"    fib=fib(n-1) + fib(n-2);\n"+
				"    return fib;\n"+
				"  }\n"+
				"}\n"+
				"\n"+
				"def int main(){\n"+
				"  int n;\n"+
				"  int fib;\n"+
				"  fib=fib(n);\n"+
				"  print fib;\n"+
				"  return fib;\n"+
				"}\n";
		ILexer lexer=new Lexer(new StringCharStream(strProgram));
		LRParser parser=new LRParser();
		ISyntaxTree syntaxTree= parser.parse(lexer, grammarPath);
		System.out.println(syntaxTree);
		
		SemanticAnalyzer semanticAnalyzer= new SemanticAnalyzer(syntaxTree);
		semanticAnalyzer.analyze();
		semanticAnalyzer.getAST().printTree();
	}

}
