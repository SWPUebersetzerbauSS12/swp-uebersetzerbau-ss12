package bufferedreader;


public class SpecialChars {

	public static final char CHAR_EOF = 0x1A;
	public static final char CHAR_SPACE = 0x20;
	public static final char CHAR_FORM_FEED = 0x0C;
	public static final char CHAR_LINE_FEED = 0x0A;
	public static final char CHAR_CARRIAGE_RETURN = 0x0D;
	public static final char CHAR_HORIZONTAL_TAB = 0x09;
	public static final char CHAR_VERTICAL_TAB = 0x0B;
	
	
	public static boolean isSpecialChar( char c) {
		switch ( c) {
			case CHAR_EOF :
			case CHAR_SPACE : 
			case CHAR_FORM_FEED :
			case CHAR_LINE_FEED :
			case CHAR_CARRIAGE_RETURN : 
			case CHAR_HORIZONTAL_TAB : 
			case CHAR_VERTICAL_TAB :
				return true; 
			default : 
				return false;			
		}
	}
	
	

	public static boolean isWhiteSpace( char c) {
		switch ( c) {
			case CHAR_SPACE : 
			case CHAR_FORM_FEED :
			case CHAR_LINE_FEED :
			case CHAR_CARRIAGE_RETURN : 
			case CHAR_HORIZONTAL_TAB : 
			case CHAR_VERTICAL_TAB :
				return true; 
			default : 
				return false;			
		}
	}
 
	
	public static boolean isNewLine( char c) {
		switch ( c) {
			case CHAR_FORM_FEED :
			case CHAR_LINE_FEED :
			case CHAR_CARRIAGE_RETURN : 
				return true; 
			default : 
				return false;			
		}
	}
 
}
