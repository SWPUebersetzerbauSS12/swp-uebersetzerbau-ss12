package regextodfaconverter;

/**
 * Stellt einen Fehler dar, der auftritt, wenn ein ungültiger regulärer Ausdruck
 * verwendet wird oder nicht unterstütze Operationen.
 * 
 * @author Daniel Rotar
 * 
 */
public class RegexInvalidException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6644434524627229729L;

	/**
	 * Erstellt ein neues RegexInvalidException Objekt.
	 */
	public RegexInvalidException() {
		super(
				"Der Ausdruck ist kein gültiger regulärer Ausdruck oder wird nicht unterstützt!");
	}
}
