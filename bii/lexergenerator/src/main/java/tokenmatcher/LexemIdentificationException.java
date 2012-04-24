package tokenmatcher;

/**
 * Stellt einen Fehler dar, der beim identifizieren eines Lexems vorkommt.
 * @author Johannes Dahlke
 * 
 */
public class LexemIdentificationException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8852563819392405779L;

	/**
	 * Erstellt ein neues LexemIdentificationException Objekt.
	 */
	public LexemIdentificationException() {
		super();
	}

	/**
	 * Erstellt ein neues LexemIdentificationException Objekt.
	 * 
	 * @param message
	 *            Die genaue Fehlerbeschreibung.
	 */
	public LexemIdentificationException(String message) {
		super(message);
	}

}
