package de.fuberlin.projectci.lrparser;

import java.util.Stack;
import java.util.logging.Logger;

import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.projectci.extern.ILexer;
import de.fuberlin.projectci.extern.ISyntaxTree;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.Production;
import de.fuberlin.projectci.grammar.Symbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;
import de.fuberlin.projectci.parseTable.AcceptAction;
import de.fuberlin.projectci.parseTable.Action;
import de.fuberlin.projectci.parseTable.ErrorAction;
import de.fuberlin.projectci.parseTable.ParseTable;
import de.fuberlin.projectci.parseTable.ReduceAction;
import de.fuberlin.projectci.parseTable.ShiftAction;
import de.fuberlin.projectci.parseTable.State;

public class Driver {
	private static Logger logger=LogFactory.getLogger(Driver.class);
	
	public ISyntaxTree parse(ILexer lexer, Grammar grammar, ParseTable parseTable) {		
		// LR-Parse-Algorithmus aus dem Drachenbuch (Algorithmus 4.30/ S. 302 in der 2. deutschen Auflage)
		// TODO LR-Parse-Algorithmus zitieren
		// erweitert um die Erzeugung des (vollständigen) Parsebaums und dessen Reduzierung auf den Abstrakten Syntaxbaum.
		
		Stack<SyntaxTreeNode> nodeStack=new Stack<SyntaxTreeNode>();
		Stack<State> stateStack=new Stack<State>();
		stateStack.push(parseTable.getInitialState());
		TerminalSymbol currentTerminalSymbol=readNextTerminalSymbol(lexer);
		
		while(true){			
			State currentState=stateStack.peek();
			Action currentAction=parseTable.getAction(currentState, currentTerminalSymbol);
			// TODO printConfiguration():String und logConfiguration:boolean
			logger.info(stateStack+" : " +currentTerminalSymbol+" : "+currentAction);
			if (currentAction instanceof ShiftAction){
				State targetState=((ShiftAction)currentAction).getTargetState();
				stateStack.push(targetState);
				currentTerminalSymbol=readNextTerminalSymbol(lexer);
				
			}
			else if (currentAction instanceof ReduceAction){
				Production p=((ReduceAction)currentAction).getProduction();
				for (int i = 0; i < p.getRhs().size(); i++) {
					stateStack.pop();
				}
				State s=stateStack.peek();
				State targetState=parseTable.getGoto(s, p.getLhs()).getTargetState();
				stateStack.push(targetState);
				
				// AST aufbauen
				// SyntaxTreeNode für das NonTerminalSymbol im Kopf der reduzierten Produktion anlegen
				SyntaxTreeNode currentNode=new SyntaxTreeNode(p.getLhs());
				for (int i = p.getRhs().size()-1; i >=0; i--) {	// Für jedes Symbol im Rumpf der reduzierten Produktion (von rechts nach links)				
					Symbol aSymbol=p.getRhs().get(i);
					// Zu jedem Symbol im Rumpf der reduzierten Produktion liegt der zugehörige Knoten (in umgedrehter Reihenfolge - s.o.) auf dem Stack 
					SyntaxTreeNode aChildNode=null;
					if (aSymbol instanceof TerminalSymbol){
						// Für TerminalSymbol einen einfachen Knoten (Blatt) anlegen
						aChildNode=new SyntaxTreeNode(aSymbol);
					}
					else if (aSymbol instanceof NonTerminalSymbol){
						// Knoten für NonTerminalSymbol vom Stack nehmen
						aChildNode=nodeStack.pop();
					}
					currentNode.insertTree(aChildNode);
				}
				// Neuen Knoten auf den Stack legen
				nodeStack.push(currentNode);				
			}
			else if (currentAction instanceof AcceptAction){
				logger.info("Done.");		
				// Wurzel des Syntaxbaums vom Stack holen und zu Abstrakten Syntaxbaum reduzieren
				// TODO Abstrakten Syntaxbaum direkt erzeugen
				SyntaxTreeNode syntaxTree= nodeStack.pop();
				syntaxTree.reduceToAbstractSyntaxTree();
				return syntaxTree;
			}
			else if (currentAction instanceof ErrorAction){
				// TODO Fehlerbehandlung implementieren
				logger.warning("Error");
				break;
			}			
		}
		logger.severe("Unexpected Error");
		return null;
	}
	 
	/** Bestimmt das TerminalSymbol für das nächste Token */
	private TerminalSymbol readNextTerminalSymbol(ILexer lexer){		
		return new TerminalSymbol(lexer.getNextToken().getType().terminalSymbol());
	}
	
	
}
 
