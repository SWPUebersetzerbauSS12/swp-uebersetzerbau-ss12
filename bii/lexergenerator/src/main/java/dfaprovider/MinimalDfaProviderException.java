package dfaprovider;

/**
 * Exceptionklasse für MinimalDfaProvider
 * 
 * @author Maximilian Schröder
 *
 */
public class MinimalDfaProviderException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6521418734139082883L;
	
	/**
	 * Erstellt ein neues MinimalDfaProviderException Objekt.
	 */
	public MinimalDfaProviderException() {
		super();
	}
	
	/**
	 * Erstellt ein neues MinimalDfaProviderException Objekt.
	 * 
	 * @param message
	 *            Die genaue Fehlerbeschreibung.
	 */
	public MinimalDfaProviderException(String message) {
		super(message);
	}
	
}
