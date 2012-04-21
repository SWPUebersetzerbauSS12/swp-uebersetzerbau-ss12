package regextodfaconverter.fsm;

import java.util.HashSet;
import java.util.UUID;

import regextodfaconverter.fsm.excpetions.TransitionAlreadyExistsException;

/**
 * Stellt einen Zustand eines endlicher Automaten (bzw. einer Zustandsmaschine) dar.
 * @author Daniel Rotar
 *
 * @param <TransitionConditionType> Der Typ der Bedingung f�r einen Zustand�bergang.
 * @param <PayloadType> Der Typ des Inhalts.
 */
public class State
	<
		TransitionConditionType extends Comparable<TransitionConditionType>,
		PayloadType
	> 
	implements 
		Comparable<State<TransitionConditionType, PayloadType>>, 
		tokenmatcher.State<PayloadType>
{
	
	/**
	 * Die eindetige UUID dieses Zustandes.
	 */
	private UUID _uuid;
	/**
	 * Der in diesem Zustand hinterlegte Inhalt.
	 */
	private PayloadType _payload;
	/**
	 * Die �berg�nge, die von diesem Zustand m�glich sind.
	 */
	private HashSet<Transition<TransitionConditionType, PayloadType>> _transitions;
	/**
	 * Der Zustandstyp.
	 */
	private StateType _type;	
	
	
	
	/**
	 * Gibt die eindetige UUID dieses Zustandes zur�ck.
	 * @return Die eindetige UUID dieses Zustandes.
	 */
	public UUID getUUID()
	{
		return _uuid;
	}
	/**
	 * Gibt den in diesem Zustand hinterlegte Inhalt zur�ck.
	 * @return Der in diesem Zustand hinterlegte Inhalt.
	 */
	public PayloadType getPayload()
	{
		return _payload;
	}
	/**
	 * Setzt den in diesem Zustand zu hinterlegenden Inhalt fest.
	 * @param payload Der in diesem Zustand zu hinterlegende Inhalt.
	 */
	public void setPayload(PayloadType payload)
	{
		_payload = payload;
	}
	/**
	 * Gibt die �berg�nge, die von diesem Zustand m�glich sind zur�ck.
	 * @return Die �berg�nge, die von diesem Zustand m�glich sind.
	 */
	public HashSet<Transition<TransitionConditionType, PayloadType>> getTransitions()
	{
		return _transitions;
	}
	/**
	 * Gibt den Zustandstyp zur�ck.
	 * @return Der Zustandstyp.
	 */
	public StateType getType()
	{
		//Ein Zustand ohne ausgehende �berg�nge ist immer ein Endzustand, es sei den er ist ein Startzustand.
		if (_type != StateType.INITIAL && _transitions.isEmpty())
		{
			return StateType.FINITE;
		}
		return _type;
	}
	/**
	 * Legt den Zustandstyp fest.
	 * @param type Der Zustandstyp.
	 */
	void setType(StateType type)
	{
		_type = type;
	}	
	/**
	 * Legt den Zustandstyp auf FINITE fest.
	 */
	public void SetTypeToFinite()
	{
		_type = StateType.FINITE;
	}
	/**
	 * Legt den Zustandstyp auf DEFAULT fest.
	 */
	public void SetTypeToDefault()
	{
		_type = StateType.DEFAULT;
	}
	/**
	 * Gibt an, ob der Zustandtyp dieses Zustands FINITE ist.
	 */
	public boolean isFiniteState() {
		if (_type == StateType.FINITE)
		{
			return true;
		}
		else
		{
			return false;
		}		
	}
	/**
	 * Gibt an, ob der Zustandtyp dieses Zustands INITIAL ist.
	 */
	public boolean isInitialState() {
		if (_type == StateType.INITIAL)
		{
			return true;
		}
		else
		{
			return false;
		}		
	}
	/**
	 * Gibt an, ob der Zustandtyp dieses Zustands Default ist.
	 */
	public boolean isDefaultState() {
		if (_type == StateType.DEFAULT)
		{
			return true;
		}
		else
		{
			return false;
		}		
	}	


	
	/**
	 * F�gt dem aktuellen Zustand einen Nachfolgezustand hinzu.
	 * @param condition Die Bedingung f�r den Zustand�bergang.
	 * @param state Der einzuf�gende Nachfolgezustand.
	 * @throws TransitionAlreadyExistsException Wenn der �bergang bereits vorhanden ist.
	 */
	void addState(TransitionConditionType condition, State<TransitionConditionType, PayloadType> state) throws TransitionAlreadyExistsException
	{
		if (!_transitions.add(new Transition<TransitionConditionType, PayloadType>(condition,state)))
		{
			throw new TransitionAlreadyExistsException();
		}
	}
	
	
	public int compareTo(State<TransitionConditionType, PayloadType> o) {
		if (o.getUUID() == getUUID())
		{
			return 0;
		}
		else
		{
			return -1;
		}		
	}
	
	
	
	/**
	 * Erstellt ein neues State Objekt.
	 */
	public State()
	{
		_uuid = UUID.randomUUID();
		_payload = null;
		_transitions = new HashSet<Transition<TransitionConditionType, PayloadType>>();
		_type = StateType.DEFAULT;
	}
	/**
	 * Erstellt ein neues State Objekt.
	 * @param payload Der in diesem Zustand hinterlegte Inhalt.
	 */
	public State(PayloadType payload)
	{
		this();
		_payload = payload;		
	}
	/**
	 * Erstellt ein neues State Objekt.
	 * @param isFinal Gibt an, ob es sich bei diesem Zustand um einen Endzustand handelt.
	 */
	public State(boolean isFinal)
	{
		this();
		if (isFinal) _type = StateType.FINITE;
	}
	/**
	 * Erstellt ein neues State Objekt.
	 * @param payload Der in diesem Zustand hinterlegte Inhalt.
	 * @param isFinal Gibt an, ob es sich bei diesem Zustand um einen Endzustand handelt.
	 */
	public State(PayloadType payload, boolean isFinal)
	{
		this();
		_payload = payload;	
		if (isFinal) _type = StateType.FINITE;
	}

}
