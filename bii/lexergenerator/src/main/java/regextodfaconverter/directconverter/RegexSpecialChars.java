package regextodfaconverter.directconverter;

public class RegexSpecialChars {

	public static final char REGEX_ALTERNATIVE_CHAR = '|';
	public static final char REGEX_MASK_CHAR = '\\';
	public static final char REGEX_CLASS_BEGIN = '[';
	public static final char REGEX_CLASS_END = ']';
	public static final char REGEX_REPETITION_BEGIN = '{';
	public static final char REGEX_REPETITION_END = '}';
	public static final char REGEX_GROUP_BEGIN = '(';
	public static final char REGEX_GROUP_END = ')';
	public static final char REGEX_KLEENE_CLOSURE = '*';
	public static final char REGEX_POSITIVE_KLEENE_CLOSURE = '+';
	public static final char REGEX_OPTION = '?';
	public static final char REGEX_JOKER = '.';

	public static final char EMPTY_STRING = 0x00;
	public static final char TERMINATOR = 0x03; // ETX = End Of Text

	/**
	 * Prüft, ob ein Zeichen ein Zeichen mit besonderer Bedeutung bezüglich regulärer Ausdrücke ist.
	 * @param theCharacter
	 * @return
	 */
	public static boolean isSpecialChar( char theCharacter) {
		switch ( theCharacter) {
			case REGEX_MASK_CHAR:
			case REGEX_GROUP_BEGIN:
			case REGEX_GROUP_END:
			case REGEX_ALTERNATIVE_CHAR:
			case REGEX_CLASS_BEGIN:
			case REGEX_CLASS_END:
			case REGEX_REPETITION_BEGIN:
			case REGEX_REPETITION_END:
			case REGEX_KLEENE_CLOSURE:
			case REGEX_POSITIVE_KLEENE_CLOSURE:
			case REGEX_OPTION:
			case REGEX_JOKER:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Ermittelt, ob ein Zeichen zu dem grundlegenden Regex Zeichensatz gehört.
	 * @param theCharacter
	 * @return
	 */
	public static boolean isElementOfBasicCharset( char theCharacter) {
		switch ( theCharacter) {
			case REGEX_MASK_CHAR:
			case REGEX_ALTERNATIVE_CHAR:
			case REGEX_KLEENE_CLOSURE:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isBasicOperator( char theChar) {
		switch ( theChar) {
			case REGEX_ALTERNATIVE_CHAR:
			case REGEX_KLEENE_CLOSURE:
				return true;
			default:
				return false;
		}
	}



	/**
	 * Prüft, ob es sich um ein leeres Wort handelt.
	 * @param theCharacter
	 * @return
	 */
	public static boolean isEmptyString( char theCharacter) {
		return EMPTY_STRING == theCharacter;
	}

}
