package TokenMatcher;


public class FiniteStateMachine {
	
	
	
	
	private State currentState;
	private State startState;
	
	
	private TransitionTable transitionTable;
	
	
	
	public FiniteStateMachine( TransitionTable transitionTable) {
		super();
		this.transitionTable = transitionTable;
	}
	
	
	/**
	 * Wechselt in einen anderen Zustand, sofern 
	 * es einen Übergang in der Übergangstabelle zu 
	 * dem angegebenen Symbol gibt. 
	 * 
	 * @param symbol Das Symbol, welches den Übergang definiert.
	 * @return 	Den neuen Zustand oder null, falls es keinen 
	 * 					Übergang in der Übergangstabelle gibt.
	 */
	public State changeStateBySymbol( Symbol symbol) {
		if ( canChangeStateBySymbol( symbol))
			return transitionTable.getNewStateOfTransitionForSymbol( currentState, symbol);
		else
		  return null;
	}
	
	
	/**
	 * Wechselt in einen gegebenen Zustand, sofern 
	 * es einen Eintrag zwischen dem aktuellen Zustand 
	 * und dem angegebenen Zustand gibt.   
	 * @param newState Der gewünschte neue Zustand
	 * @return Das Symbol, welches für den Übergangswechsel 
	 * 					"gelesen" wurde, oder null, falls es keinen 
	 * 					Übergang und damit auch keinen Zustandswechel gab.  
	 */
	public Symbol changeToState( State newState) {
		if ( transitionTable.hasTransition( currentState, newState))
			return transitionTable.getSymbolOfTransition( currentState, newState); 
		else 
			return null;
	}
	
	/**
	 * Prüft, ob ein Übergang für das Lesen des 
	 * spezifizierten Symbols definiert ist. 
	 * @param symbol Das Symbol, für das geprüft werden soll, ob ein Übergang 
	 *               aus dem aktuellen Zustand durch Lesen des Symbols definiert ist.
	 * @return true, wenn es einen Übergang gibt, anderenfalls false.
	 */
	public boolean canChangeStateBySymbol( Symbol symbol) {
		return transitionTable.hasTransitionForSymbol( currentState, symbol);
	}
	
	
	/**
	 * @return Liefert den aktuellen Zustand.
	 */
	public State getCurrentState() {
		return currentState;
	}
	
	
	
	
	
	
	

}
