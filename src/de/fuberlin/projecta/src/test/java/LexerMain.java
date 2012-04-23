import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import lexer.ILexer;
import lexer.IToken.TokenType;
import lexer.Lexer;
import lexer.SyntaxErrorException;
import lexer.Token;
import lexer.io.FileCharStream;
import lexer.io.StringCharStream;

public class LexerMain {

	private static void readStdin() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String data = "";
		String line;
		try {
			while ((line = in.readLine()) != null && line.length() != 0) {
				data += line + '\n';
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

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
		Token t;
		try {
			do {
				t = lexer.getNextToken();
				System.out.println(t);
			} while (t.getType() != TokenType.EOF);
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
		}
		else if (args.length == 1) {
			final String path = args[0];
			readFile(path);
		}
		else {
			System.out.println("Wrong number of arguments!");
		}
	}

}
