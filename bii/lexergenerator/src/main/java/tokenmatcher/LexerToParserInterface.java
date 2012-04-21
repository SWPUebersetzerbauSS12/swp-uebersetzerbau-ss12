package tokenmatcher;

import bufferedreader.EndOfFileException;


public interface LexerToParserInterface {
	
	Token getNextToken() throws EndOfFileException;

}
