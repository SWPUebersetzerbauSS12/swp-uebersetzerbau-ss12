/*
 * 
 * Copyright 2012 lexergen.
 * This file is part of lexergen.
 * 
 * lexergen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * lexergen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with lexergen.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * lexergen:
 * A tool to chunk source code into tokens for further processing in a compiler chain.
 * 
 * Projectgroup: bi, bii
 * 
 * Authors: Johannes Dahlke
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package bufferedreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import utils.Notification;


public class SimpleLexemeReader implements LexemeReader {
	
	private RandomAccessFile file;
	
	private long lexemeBeginMarker;
	private String sourceFile;
	
	public SimpleLexemeReader( String SourceFile) throws LexemeReaderException {
		this.sourceFile = sourceFile;
		reopen();
	}

	public char getNextChar() throws LexemeReaderException {
		try {
			// Wenn nicht bereits zuvor EOF zurückgegeben wurde, dann spätestens jetzt.
			if ( file.getFilePointer() >= file.length())
			  return SpecialChars.CHAR_EOF;
			// anderenfalls gib das aktuelle Zeichen zurück.
			return (char) file.read();
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
			throw new LexemeReaderException( "Cannot step backward lexem.");
		}
	}
	
	
	public void reopen() throws LexemeReaderException {
	  try {
			// we open the file read only
			file = new RandomAccessFile( sourceFile, "r");
			lexemeBeginMarker = file.getFilePointer();
	  } catch ( Exception e) {
	  	Notification.printDebugException( e);
	  	throw new LexemeReaderException( String.format("Cannot open the source file '%s'.", sourceFile));
		}
	}

}
