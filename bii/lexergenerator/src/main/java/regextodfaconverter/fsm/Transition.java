package regextodfaconverter.fsm;

/**
 * Stellt einen �bergang eines endlicher Automaten (bzw. einer Zustandsmaschine) dar.
 * @author Daniel Rotar
 *
 * @param <ConditionType> Der Typ der Bedingung f�r den Zustand�bergang.
 * @param <StatePayloadType> Der Typ des Inhalts der Zust�nde.
 */
public class Transition
	<
		ConditionType extends Comparable<ConditionType>,
		StatePayloadType
	> 
	implements 
		Comparable<Transition<ConditionType, StatePayloadType>>
{
	
	/**
	 * Die Bedingung f�r den Zustand�bergang (null f�r einen Epsilon-�bergang).
	 */
	private ConditionType _condition;
	/**
	 * Der Folgezustand.
	 */
	private State<ConditionType, StatePayloadType> _state;
	
	
	
	/**
	 * Gibt die Bedingung f�r den Zustand�bergang zur�ck.
	 * @return Die Bedingung f�r den Zustand�bergang (null f�r einen Epsilon-�bergang).
	 */
	public ConditionType getCondition()
	{
		return _condition;
	}
	/**
	 * Gibt den Folgezustand zur�ck.
	 * @return Der Folgezustand.
	 */
	public State<ConditionType, StatePayloadType> getState()
	{
		return _state;
	}


	public int compareTo(Transition<ConditionType, StatePayloadType> o) {
		if (o.getCondition() == getCondition() && o.getState() == getState())
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
	
	
	
	/**
	 * Erstellt ein neues Transition Objekt.
	 * @param condition Die Bedingung f�r den Zustand�bergang (null f�r einen Epsilon-�bergang).
	 * @param state Der Folgezustand.
	 */
	public Transition(ConditionType condition, State<ConditionType, StatePayloadType> state)
	{
		_condition = condition;
		_state = state;
	}
	
}
