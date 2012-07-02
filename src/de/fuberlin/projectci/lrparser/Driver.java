package de.fuberlin.projectci.lrparser;

import java.util.Stack;
import java.util.logging.Logger;

import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.parser.ISyntaxTree;
//import de.fuberlin.projectci.extern.IToken;
//import de.fuberlin.projectci.extern.IToken.TokenType;
import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.projectci.extern.lexer.Token;
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
	// Speichert das aktuelle Eingabe-Token, wenn ein ε-Übergang durchgeführt wird
	private IToken storedToken=null;
	
	public ISyntaxTree parse(ILexer lexer, Grammar grammar, ParseTable parseTable) {		
		// LR-Parse-Algorithmus aus dem Drachenbuch (Algorithmus 4.30/ S. 302 in der 2. deutschen Auflage)
		// TODO LR-Parse-Algorithmus zitieren
		// erweitert um die Erzeugung des (vollständigen) Parsebaums und dessen Reduzierung auf den Abstrakten Syntaxbaum.
		// Speichert die gelesenen Token um sie mit den Terminalknoten zu verknüpfen
		Stack<IToken> tokenStack= new Stack<IToken>();
		Stack<SyntaxTreeNode> nodeStack=new Stack<SyntaxTreeNode>();
		Stack<State> stateStack=new Stack<State>();
		stateStack.push(parseTable.getInitialState());
		IToken currentToken=readNextToken(lexer);
//		tokenStack.push(currentToken);
		TerminalSymbol currentTerminalSymbol=new TerminalSymbol(currentToken.getText());
		
		while(true){			
			State currentState=stateStack.peek();
			Action currentAction=parseTable.getAction(currentState, currentTerminalSymbol);
			// TODO printConfiguration():String und logConfiguration:boolean
			logger.info(stateStack+" : " +currentToken+" : "+currentAction);
			if (currentAction instanceof ShiftAction){
				State targetState=((ShiftAction)currentAction).getTargetState();
				stateStack.push(targetState);
				tokenStack.push(currentToken);
				currentToken=readNextToken(lexer);				
				currentTerminalSymbol=new TerminalSymbol(currentToken.getText());
				
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
						// Für TerminalSymbol einen einfachen Knoten (Blatt) mit dem zugehörigen Token anlegen
						aChildNode=new SyntaxTreeNode(tokenStack.pop(), (TerminalSymbol) aSymbol);
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
				// Wurzel des Parsebaums vom Stack holen 
				SyntaxTreeNode syntaxTree= nodeStack.pop();				
				return syntaxTree;
			}
			else if (currentAction instanceof ErrorAction){
				// Zum aktuellen Eingabe-Token konnte keine gültige Action ermittelt werden
				// Daher wird jetzt erstmal versucht ob es eine Action für ε gibt.
				if (!(parseTable.getAction(currentState, Grammar.EPSILON) instanceof ErrorAction)){
					// Es gibt eine Action für ε --> Aktuelles Eingabetoken zurückstellen
					// TODO Prüfen, ob dies die richtige Art und Weise ist um einen ε-Übergang herzustellen
					// (Der Syntaxbaum für ein einfaches Programm sah jedenfalls richtig aus...)
					storedToken=currentToken;
					// FIXME TokenType.EPSILON gibt es nicht mehr, wurde durch null ersetzt
					currentToken=new Token(null, null, currentToken.getLineNumber(), currentToken.getOffset());
					currentTerminalSymbol=Grammar.EPSILON;
//					if (!tokenStack.isEmpty()){
//						tokenStack.pop();
//					}
//					tokenStack.push(currentToken);
					continue;
				}
				
				// TODO Fehlerbehandlung implementieren
				System.out.println("Parse Error: unexpected Token "+currentToken.getText()+" in line "+currentToken.getLineNumber()+":"+currentToken.getOffset());
				System.out.print("Possible Tokens: ");
				for(TerminalSymbol t : parseTable.getActionTableForState(currentState).getAllLegalTerminals()) {
					System.out.print(t.toString()+" ");
				}
				System.out.println();
				
				//logger.warning("Error");
				break;
			}			
		}
		//logger.severe("Unexpected Error");
		return null;
	}
	
	
	/** Liest das nächste Token */
	private IToken readNextToken(ILexer lexer){	
		if (storedToken!=null){
			IToken result=storedToken;
			storedToken=null;
			return result;
		}
		IToken nextToken=lexer.getNextToken();
		return nextToken;
	}
	
	
}
 
