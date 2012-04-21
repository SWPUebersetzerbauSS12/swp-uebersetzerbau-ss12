package regextodfaconverter.fsm.excpetions;

/**
 * Stellt einen Fehler dar, der auftritt, wenn versucht wird mit einem Zustand zu Arbeiten der nicht erreichbar oder Teil des endlichen Automats ist.
 * @author Daniel Rotar
 *
 */
public class StateNotReachableException extends Exception
{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7285394240271883235L;

	
	
	/**
	 * Erstellt ein neues StateNotReachableException Objekt.
	 */
	public StateNotReachableException()
	{
		super("Der Zustand kann nicht erreicht werden oder ist nicht Teil des endlichen Automats!");
	}
	
}
