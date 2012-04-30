package dfaprovider;

public class MinimalDfaProviderException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6521418734139082883L;
	
	/**
	 * Erstellt ein neues MinimalDfaBuilderException Objekt.
	 */
	public MinimalDfaProviderException() {
		super();
	}
	
	/**
	 * Erstellt ein neues MinimalDfaBuilderException Objekt.
	 * 
	 * @param message
	 *            Die genaue Fehlerbeschreibung.
	 */
	public MinimalDfaProviderException(String message) {
		super(message);
	}
	
}
