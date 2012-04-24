package regextodfaconverter;

/**
 * Stellt einen Fehler dar, der beim Konvertieren eines regulären Ausdrucks in
 * einen DFA vorkommt.
 * 
 * @author Johannes Dahlke
 * 
 */
public class ConvertExecption extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2213213933640594695L;

	/**
	 * Erstellt ein neues ConvertExecption Objekt.
	 */
	public ConvertExecption() {
		super();
	}

	/**
	 * Erstellt ein neues ConvertExecption Objekt.
	 * 
	 * @param message
	 *            Die genaue Fehlerbeschreibung.
	 */
	public ConvertExecption(String message) {
		super(message);
	}

}
