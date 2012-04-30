package dfaprovider;

/**
 * Stellt einen Fehler dar, der auftritt, wenn der MinimalDfaBuilder beim Erstellungsprozess des DFAs scheitert.
 * @author Daniel
 *
 */
public class MinimalDfaBuilderException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4620566821912145123L;

	/**
	 * Erstellt ein neues MinimalDfaBuilderException Objekt.
	 */
	public MinimalDfaBuilderException() {
		super();
	}

	/**
	 * Erstellt ein neues MinimalDfaBuilderException Objekt.
	 * 
	 * @param message
	 *            Die genaue Fehlerbeschreibung.
	 */
	public MinimalDfaBuilderException(String message) {
		super(message);
	}

}
