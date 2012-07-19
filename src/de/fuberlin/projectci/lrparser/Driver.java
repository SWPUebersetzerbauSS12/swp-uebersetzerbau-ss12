package de.fuberlin.projectci.lrparser;

import java.util.Stack;
import java.util.logging.Logger;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.commons.util.LogFactory;
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
	/** Speichert das aktuelle Eingabe-Token, wenn ein ε-Übergang durchgeführt wird */
	private IToken storedToken=null;
	
	
	/**
	 * Implementiert den LR-Parse-Algorithmus aus dem Drachenbuch (Algorithmus 4.30/ S. 302 in der 2. deutschen Auflage).
	 * Erweitert um die Erzeugung des (vollständigen) Parsebaums und dessen Reduzierung auf den Abstrakten Syntaxbaum.
	 * Erweitert um die Behandlung von ε-Übergängen
	 * Speichert die gelesenen Token um sie mit den Terminalknoten zu verknüpfen.
	 * LR-Parse-Algorithmus aus dem Drachenbuch :
	 <code>
	 	Eingabe: ein Eingabestring w und eine LR-Parsertabelle mit den Funtkionen ACTION und GOTO für eine Grammatik G
		Ausgabe: wenn w in L(G) ist, die Reduzierungsschritte einer Bottom-Up-Analyse für w, andernfalls eine Fehlermeldung
		Methode: Zunächst liegt s0, der Ausgangszustand, auf dem Stack und w$ befindet sich im Eingabepuffer. Dann führt der Parser das folgende Programm aus:

		Sei a das erste Symbol von w$;
		while(1){
			Sei s der oberste Zustand auf dem Stack;
			if (ACTION[s,a] = shift t){
				verschiebe t auf den Stack;
				Sei a das nächste Eingabesymbol;
			} 
			else if (ACTION[s,a] = reduce A → β){
				Entferne |β| Symbole vom Stack;
				Zustand t liegt jetzt oben auf dem Stack;
				verschiebe GOTO[t,A] auf den Stack;
				gib die Produktion A → β aus; 
			}
			else if (ACTION[s,a] = accept){
				break; // Analyse ist beendet
			}
			else{
				Fehlerbehandlung aufrufen;
			}
		}
	 </code>
	 * @param lexer ILexer für die Eingabe
	 * @param parseTable LR-Parsertabelle mit den Funtkionen ACTION und GOTO
	 * @return den vollständigen (konkreten) Parsebaum
	 * @throws LRParserException falls kein Parsebaum erzeugt werden konnte.
	 */
	public ISyntaxTree parse(ILexer lexer, ParseTable parseTable) {		
		
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
			logger.finer(stateStack+" : " +currentToken+" : "+currentAction);
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
				logger.finer("Done.");		
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
					currentToken=new EpsilonToken(currentToken.getLineNumber(), currentToken.getOffset());
					currentTerminalSymbol=Grammar.EPSILON;
//					if (!tokenStack.isEmpty()){
//						tokenStack.pop();
//					}
//					tokenStack.push(currentToken);
					continue;
				}
				
				logger.warning("Parse Error: Unexpected Token "+currentToken.getText()+" in line "+currentToken.getLineNumber()+":"+currentToken.getOffset());
				StringBuffer strBufPossibleTokens=new StringBuffer();					
				int i=0;
				for(TerminalSymbol t : parseTable.getActionTableForState(currentState).getAllLegalTerminals()) {
					if (i>0)strBufPossibleTokens.append(", ");
					strBufPossibleTokens.append(t);
					i++;
				}
				logger.warning("Parse Error: Expected Tokens are: "+strBufPossibleTokens);								
				throw new LRParserException("Parse Error: Unexpected Token "+currentToken.getText()+" in line "+currentToken.getLineNumber()+":"+currentToken.getOffset());
			}			
		}
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
	
	private static class EpsilonToken implements IToken{
		private int lineNumber;
		private int offset;
		
		public EpsilonToken(int lineNumber, int offset) {
			this.lineNumber=lineNumber;
			this.offset=offset;
		}

		@Override
		public String getType() {
			// EpsilonToken wird nur intern verwendet und type brauchen wir nicht...
			return null;
		}

		@Override
		public String getText() {
			return Grammar.EMPTY_STRING;
		}

		@Override
		public Object getAttribute() {			
			return null;
		}

		@Override
		public int getOffset() {
			return offset;
		}

		@Override
		public int getLineNumber() {
			return lineNumber;
		}
		
		public String toString(){
			return "<" + getText() + ", >";
		}
		
	}
	
}
 
