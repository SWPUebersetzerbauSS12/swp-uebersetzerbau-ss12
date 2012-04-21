package bufferedreader;


public class EndOfFileException extends Exception {

	
	public EndOfFileException() {
		super( "End of file.");
	}
	
	public EndOfFileException( String message) {
		super( message);
	}
}
