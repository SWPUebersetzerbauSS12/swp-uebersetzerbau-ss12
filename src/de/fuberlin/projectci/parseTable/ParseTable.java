package de.fuberlin.projectci.parseTable;

import java.util.HashMap;
import java.util.Map;

import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;

public class ParseTable {
	private Map<State, ActionTable> state2ActionTable = new HashMap<State, ParseTable.ActionTable>();
	private Map<State, GotoTable> state2GotoTable = new HashMap<State, ParseTable.GotoTable>();
	private State initialState;
	
	public State getInitialState(){
		return initialState;
	}

	public Action getAction(State s, TerminalSymbol nts) {
		return state2ActionTable.get(s).getAction(nts);
	}
	 
	public Goto getGoto(State s, NonTerminalSymbol ts) {
		return state2GotoTable.get(s).getGoto(ts);
	}
	
	public void setInitialState(State initialState){
		this.initialState=initialState;
	}
	
	public ActionTable getActionTableForState(State s){
		if (!state2ActionTable.containsKey(s)){
			state2ActionTable.put(s, new ActionTable());
		}
		return state2ActionTable.get(s);
	}
	
	public GotoTable getGotoTableForState(State s){
		if (!state2GotoTable.containsKey(s)){
			state2GotoTable.put(s, new GotoTable());
		}
		return state2GotoTable.get(s);
	}
	
	public static class ActionTable{
		private Map<TerminalSymbol, Action> terminalSymbol2Action= new HashMap<TerminalSymbol, Action>();
		
		public Action getAction(TerminalSymbol nts) {
			Action a= terminalSymbol2Action.get(nts);
			return a!=null?a:new ErrorAction();
		}
		
		public void setActionForTerminalSymbol(Action action, TerminalSymbol terminalSymbol){
			terminalSymbol2Action.put(terminalSymbol, action);
		}
	}
	
	public static class GotoTable{
		private Map<NonTerminalSymbol, Goto> nonTerminalSymbol2Goto= new HashMap<NonTerminalSymbol, Goto>();
		
		public Goto getGoto(NonTerminalSymbol nonTerminalSymbol){
			return nonTerminalSymbol2Goto.get(nonTerminalSymbol);
		}
		
		public void setGotoForNonTerminalSymbol(Goto _goto, NonTerminalSymbol nonTerminalSymbol){
			nonTerminalSymbol2Goto.put(nonTerminalSymbol, _goto);
		}
	}
	
}
 
