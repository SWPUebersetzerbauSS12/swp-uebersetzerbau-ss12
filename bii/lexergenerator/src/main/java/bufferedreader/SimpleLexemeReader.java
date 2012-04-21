package bufferedreader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import utils.Notification;


public class SimpleLexemeReader implements LexemeReader {
	
	private RandomAccessFile file;
	
	private long lexemeBeginMarker;
	
	public SimpleLexemeReader( String SourceFile) throws IOException {
		// we open the file read only
		file = new RandomAccessFile( SourceFile, "r");
		lexemeBeginMarker = file.getFilePointer();
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
