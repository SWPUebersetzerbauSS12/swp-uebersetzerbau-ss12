package tokenmatcher.fsm;

import utils.Test;



public class FiniteStateMachine<E> {
	
	private State currentState;
	
	
	private TransitionTable<E> transitionTable = null;
	
	
	
	public FiniteStateMachine( TransitionTable<E> transitionTable) throws Exception {
		super();
		if ( Test.isUnassigned( transitionTable))
			throw new Exception( "No transition table forwarded.");
		this.transitionTable = transitionTable;
		this.currentState = transitionTable.getStartState();
	}
	
	/*
	public FiniteStateMachine() {
		super();
		this.transitionTable = new TransitionTable();
	}
	*/
	
	
	/**
	 * Wechselt in einen anderen Zustand, sofern 
	 * es einen Übergang in der Übergangstabelle zu 
	 * dem angegebenen Element gibt. 
	 * 
	 * @param element Das Element, welches den Übergang definiert.
	 * @return 	Den neuen Zustand oder null, falls es keinen 
	 * 					Übergang in der Übergangstabelle gibt.
	 */
	public State changeStateByElement( E element) {
		if ( canChangeStateByElement( element))
			return transitionTable.getNewStateOfTransitionForElement( currentState, element);
		else
		  return null;
	}
	
	
	/**
	 * Wechselt in einen gegebenen Zustand, sofern 
	 * es einen Eintrag zwischen dem aktuellen Zustand 
	 * und dem angegebenen Zustand gibt.   
	 * @param newState Der gewünschte neue Zustand
	 * @return Das Element , welches für den Übergangswechsel 
	 * 					"gelesen" wurde, oder null, falls es keinen 
	 * 					Übergang und damit auch keinen Zustandswechel gab.  
	 */
	public E changeToState( State newState) {
		if ( transitionTable.hasTransition( currentState, newState))
			return transitionTable.getElementOfTransition( currentState, newState); 
		else 
			return null;
	}
	
	/**
	 * Prüft, ob ein Übergang für das Lesen des 
	 * spezifizierten Elementes definiert ist. 
	 * @param element Das Element, für das geprüft werden soll, ob ein Übergang 
	 *               aus dem aktuellen Zustand durch Lesen des Elementes definiert ist.
	 * @return true, wenn es einen Übergang gibt, anderenfalls false.
	 */
	public boolean canChangeStateByElement( E element) {
		return transitionTable.hasTransitionForElement( currentState, element);
	}
	
	
	/**
	 * @return Liefert den aktuellen Zustand.
	 */
	public State getCurrentState() {
		return currentState;
	}
	
	
	
	
	
	
	

}
