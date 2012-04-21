package bufferedreader;


public interface LexemeReader {
	
	/**
	 * Liest das nächste Zeichen von der Eingabe. Am Ende des Eingabestroms 
	 * wird das {@link SpecialChars#CHAR_EOF} geliefert. 
	 * @return das nächste Zeichen.
	 * @throws LexemeReaderException wenn es zu einen IO Fehler kam. 
	 */
	char getNextChar() throws LexemeReaderException;
	
	
	/**
	 * Setzt den Positionszeiger des Lesers hinter das Ende des 
	 * zuletzt akzeptierten Lexems. 
	 * @throws LexemeReaderException 
	 */
	void reset() throws LexemeReaderException;
	
	
	/**
	 * Acceptiert das zuletzt gelesene Lexem, indem es den Marker lexemeBegin 
	 * genau hinter dem zuletzt gelesenen Lexem positioniert. 
	 * @throws LexemeReaderException 
	 */
	void accept() throws LexemeReaderException;
	
	/**
	 * Setzt den Zeiger der aktuellen Leseposition um die angegebene Anzahl an Schritten zurück. 
	 * @param steps die Anzahl der Schritte
	 * @throws LexemeReaderException 
	 */
	void stepBackward( int steps) throws LexemeReaderException;
	

}
