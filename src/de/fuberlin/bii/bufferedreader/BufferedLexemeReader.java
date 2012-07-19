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

package de.fuberlin.bii.bufferedreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.fuberlin.bii.utils.Notification;

/**
 * Eingabeleser arbeitet mit einem Pufferpaar und Wächtern 
 * wie im Drachenbuch beschrieben.
 * 
 * TODO test it. Reader ist noch nicht vollständig getestet.
 * 
 * @author Johannes Dahlke
 *
 */
public class BufferedLexemeReader implements LexemeReader {
	
	private static final int NUM_BUFFER_SIZE = 1024;
	private static final char EOF_CHAR = 0x1A;   
	
	private File file;
	private String sourceFile;
	
	private int lexemeBeginMarker;
	private int forwardPosition;
	private char[] buffer1 = new char[NUM_BUFFER_SIZE];
	private char[] buffer2 = new char[NUM_BUFFER_SIZE];
	private FileReader fileReader;
	private boolean endOfFileReached = false;
	

	public BufferedLexemeReader( String sourceFile) throws LexemeReaderException {
		super();
		this.sourceFile = sourceFile;
		reopen();
	}
	
	private void initBuffer() {
	  readNextBlockIntoBuffer( buffer1);
	  // Initialize file pointer
	  lexemeBeginMarker = 0;
	  forwardPosition = 0;
	}
	
	private char readCharFromBufferAtPosition( int position) {
	  if ( position >= NUM_BUFFER_SIZE) 
	    return buffer2[ forwardPosition % NUM_BUFFER_SIZE];
	  else
	    return buffer1[ forwardPosition];
	}
	
	

	private int readNextBlockIntoBuffer( char[] buffer) { 
	  // read a block, but leave room for the EOF
		int readedChars;
		try {
			readedChars = fileReader.read( buffer, 0, NUM_BUFFER_SIZE -1);
		} catch ( IOException e) {
			Notification.printDebugException( e);
			buffer[0] = EOF_CHAR;
			return 0;
		}
		if ( readedChars == -1) {
		  // the end of file is reached
			buffer[0] = EOF_CHAR;
			readedChars = 0;
		}	else {
			if ( readedChars < NUM_BUFFER_SIZE -1)
        // the end of file must be reached after readedChars chars
        buffer[readedChars] = EOF_CHAR;
		}
		// anyway mark the end of buffer with EOF
		buffer[NUM_BUFFER_SIZE-1] = EOF_CHAR;
		return readedChars;
	}
	
	private boolean isEndOfFile() {
	  return endOfFileReached;
	}   
			
	
	private static int getCurrentPosition() throws Exception {
	  throw new Exception( "Not yet implemented");
	}   
	
	
	/**
	 * Liefert das nächste Zeichen der Eingabe.
	 * 
	 * @return das nächste Zeichen.
	 * @throws EndOfFileException 
	 * @throw LexemeReaderException wenn das Ende der Datei erreicht ist oder 
	 * ein IO-Fehler beim Lesen der Datei aufgetreten ist.
	 */
	public char getNextChar() throws LexemeReaderException {
		char result;
		char readedChar = readCharFromBufferAtPosition( forwardPosition);
		if ( readedChar == EOF_CHAR) {
			if ( forwardPosition / (NUM_BUFFER_SIZE -1) == 1) {
			  // Read pointer is at the end of buffer1
        if ( lexemeBeginMarker >= NUM_BUFFER_SIZE)
            throw new LexemeReaderException( "Oversized lexeme. Accept lexeme first.");
          // reload the buffer 2
          readNextBlockIntoBuffer( buffer2);
          // set pointer to the begin of buffer 2 simple by recall this function
          result = getNextChar();
			} else if ( forwardPosition / ( NUM_BUFFER_SIZE -1) == 2) {
        // Read pointer is at the end of buffer 2
        if ( lexemeBeginMarker < NUM_BUFFER_SIZE) 
        	throw new LexemeReaderException( "Oversized lexeme. Accept lexeme first.");
        // reload the buffer 1
        readNextBlockIntoBuffer( buffer1);
        // set pointer to the begin of buffer 1
        forwardPosition = 0;
        // and then recall this function
        result = getNextChar();
			} else {
				endOfFileReached = true;
			  // the readed EOF is the real EOF of the file
		  	// return it (to stop the lexical analysis)
		  	result = EOF_CHAR;  
			}
		} else
		    // otherwise return the readed char
		  result = readedChar;

		// do not move over the end
		if ( result != SpecialChars.CHAR_EOF) 
			forwardPosition++;
		
		return result;	
	}

	
	/**
	 * Setzt den Positionszeiger des Lesers hinter das Ende des 
	 * zuletzt akzeptierten Lexems. 
	 * @throws LexemeReaderException 
	 */
	public void reset() throws LexemeReaderException {
	  forwardPosition = lexemeBeginMarker -1;
	  endOfFileReached = false;       
	}

	/**
	 * Akzeptiert das zuletzt gelesene Lexem, indem es den Marker {@link #lexemeBeginMarker} 
	 * genau hinter dem zuletzt gelesenen Lexem positioniert. 
	 * @throws LexemeReaderException 
	 */
	public void accept() throws LexemeReaderException {
		 if ( forwardPosition == NUM_BUFFER_SIZE -2) {
		   lexemeBeginMarker = NUM_BUFFER_SIZE;
		 } else if ( forwardPosition == 2 *NUM_BUFFER_SIZE -2) {
			 lexemeBeginMarker = 0;
		 } else {
			 lexemeBeginMarker = forwardPosition +1;
		 }
	}
	
	/**
	 * Setzt den Zeiger der aktuellen Leseposition um die angegebene Anzahl an Schritten zurück. 
	 */
	public void stepBackward( int steps) throws LexemeReaderException {
		// todo: check if this func is error free
		forwardPosition = Math.max( lexemeBeginMarker-1, forwardPosition -steps);      
	}

	/**
	 * Öffnet die Eingabedatei erneut. Der nächste Aufruf von {@link #getNextChar()} 
	 * liefert dann das erste Zeichen aus der Quelldatei.  
	 * @throws LexemeReaderException 
	 */
	public void reopen() throws LexemeReaderException {
	  try {
	    // we open the file read only
		  file = new File( sourceFile);
		  fileReader = new FileReader( file);
			initBuffer();
	  } catch ( FileNotFoundException e) {
	  	Notification.printDebugException( e);
	  	throw new LexemeReaderException( String.format("Cannot open the source file '%s'.", sourceFile));
		}
	}

}
