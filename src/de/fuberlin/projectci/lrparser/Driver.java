package de.fuberlin.projectci.lrparser;

import java.util.Stack;

import de.fuberlin.projectci.extern.ILexer;
import de.fuberlin.projectci.extern.ISyntaxTree;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.Production;
import de.fuberlin.projectci.grammar.TerminalSymbol;
import de.fuberlin.projectci.parseTable.AcceptAction;
import de.fuberlin.projectci.parseTable.Action;
import de.fuberlin.projectci.parseTable.ErrorAction;
import de.fuberlin.projectci.parseTable.ParseTable;
import de.fuberlin.projectci.parseTable.ReduceAction;
import de.fuberlin.projectci.parseTable.ShiftAction;
import de.fuberlin.projectci.parseTable.State;

public class Driver {
	
	public ISyntaxTree parse(ILexer lexer, Grammar grammar, ParseTable parseTable) {
		// LR-Parse-Algorithmus aus dem Drachenbuch (Algorithmus 4.30/ S. 302 in der 2. deutschen Auflage)
		Stack<State> stack=new Stack<State>();
		stack.push(parseTable.getInitialState());
		TerminalSymbol currentTerminalSymbol=readNextTerminalSymbol(lexer);
		
		while(true){
			
			State currentState=stack.peek();
			Action currentAction=parseTable.getAction(currentState, currentTerminalSymbol);
			System.out.println(stack+" : " +currentTerminalSymbol+" : "+currentAction);
			if (currentAction instanceof ShiftAction){
				State targetState=((ShiftAction)currentAction).getTargetState();
				stack.push(targetState);
				currentTerminalSymbol=readNextTerminalSymbol(lexer);
				
			}
			else if (currentAction instanceof ReduceAction){
				Production p=((ReduceAction)currentAction).getProduction();
				for (int i = 0; i < p.getRhs().size(); i++) {
					stack.pop();
				}
				State s=stack.peek();
				State targetState=parseTable.getGoto(s, p.getLhs()).getTargetState();
				stack.push(targetState);
				// TODO AST konstruieren  
			}
			else if (currentAction instanceof AcceptAction){
				System.out.println("Done.");
				break;
			}
			else if (currentAction instanceof ErrorAction){
				// TODO Fehlerbehandlung implementieren
				System.err.println("Error");
				break;
			}
			
		}
		
		return null;
	}
	 
	
	private TerminalSymbol readNextTerminalSymbol(ILexer lexer){		
		return new TerminalSymbol(lexer.getNextToken().getType().terminalSymbol());
	}
	
	
}
 
