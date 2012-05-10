package dfaprovider;

/**
 * Stellt einen Fehler dar, der auftritt, wenn im
 * MinimalDfaCharacterStatePayloadWrapper ein Fehler vorliegt.
 * 
 * Mögliche Fehlerfälle sind: - Fehler beim Deserialisieren des
 * {@link MinimalDfaCharacterStatePayloadWrapper} - laden aus der übergebenen
 * Datei nicht möglich bzw. fehlerhaft - Cast nach
 * {@link MinimalDfaCharacterStatePayloadWrapper}" schlägt fehl
 * 
 * @author Maximilian Schröder
 * 
 */
public class MinimalDfaCharacterStatePayloadWrapperException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5365197771428767919L;

	/**
	 * Erstellt ein neues MinimalDfaCharacterStatePayloadWrapperException
	 * Objekt.
	 */
	public MinimalDfaCharacterStatePayloadWrapperException() {
		super();
	}

	/**
	 * Erstellt ein neues MinimalDfaCharacterStatePayloadWrapperException
	 * Objekt.
	 * 
	 * @param message
	 *            Die genaue Fehlerbeschreibung.
	 */
	public MinimalDfaCharacterStatePayloadWrapperException(String message) {
		super(message);
	}
}
