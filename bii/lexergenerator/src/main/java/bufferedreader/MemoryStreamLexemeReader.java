package bufferedreader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;

import utils.Notification;


public class MemoryStreamLexemeReader implements LexemeReader {
	

	
	private long lexemeBeginMarker;
	
	public MemoryStreamLexemeReader( String SourceFile) throws IOException {
		// we open the file read only
		File file = new File( SourceFile, "r");
		loadFileIntoMemory( file);
		lexemeBeginMarker = file.getFilePointer();
	}
	

	private void loadFileIntoMemory( File file) {
		char[] buffer = new char[1024]; 
		file.get
		FileReader fileReader = new FileReader( file);
		fileReader.read( buffer);
		
		while 
		
		
	}


	public char getNextChar() throws LexemeReaderException {
		try {
			// Wenn nicht bereits zuvor EOF zurückgegeben wurde, dann spätestens jetzt.
			if ( file.getFilePointer() >= file.length())
			  return SpecialChars.CHAR_EOF;
			// anderenfalls gib das aktuelle Zeichen zurück.
			return file.readChar();
		} catch ( IOException e) {
			Notification.printDebugException( e);
			throw new LexemeReaderException( "Cannot read next char.");
		}
	}

	public void reset() throws LexemeReaderException {
		try {
			file.seek( lexemeBeginMarker);
		} catch ( IOException e) {
			Notification.printDebugException( e);
			throw new LexemeReaderException( "Cannot seek to lexem begin marker.");
		}
	}

	public void accept() throws LexemeReaderException {
		try {
			lexemeBeginMarker = file.getFilePointer();
		} catch ( IOException e) {
			Notification.printDebugException( e);
			throw new LexemeReaderException( "Cannot accept lexem.");
		}
	}

	public void stepBackward( int steps) throws LexemeReaderException {
		try {
			file.seek( file.getFilePointer() -steps);
		} catch ( IOException e) {
			Notification.printDebugException( e);
			throw new LexemeReaderException( "Cannot accept lexem.");
		}
	}
	
	
	
}
