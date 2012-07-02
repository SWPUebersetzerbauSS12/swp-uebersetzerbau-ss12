package de.fuberlin.projectci.parseTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.Production;
import de.fuberlin.projectci.grammar.Symbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;

/**
 * Baut eine SLR-Parsetabelle (Action- und Goto-Tabelle) zu einer erweiterten Grammatik.
 */
public class SLRParseTableBuilder extends ParseTableBuilder {
	private static Logger logger = LogFactory.getLogger(SLRParseTableBuilder.class);
	private static final String ELSE="else";
	
	public SLRParseTableBuilder(Grammar grammar) {
		super(grammar);
	}
	 
	/**
	 *Vgl. Drachenbuch: Algorithmus 4.32
	 *
	 */
	public ParseTable buildParseTable() throws InvalidGrammarException {
		
//		1. Konstruieren Sie C={I0, I1, ..., In}, die Sammlung der LR(0)-Item-Mengen für G0
//		2. Der Zustand i wird aus Ii erstellt. Die Parseraktionen für Zustand i werden wie folgt ermittelt:
//			a) Wenn [A → α.aβ] in Ii und GOTO(Ii,a) = Ij, dann setze ACTION[i,a] auf "shift j". Hier muss a ein Terminal sein.
//			b) Wenn [A → α.] in Ii, setze ACTION[i,a] für alle a in FOLLOW(A) auf "reduce A → α". Hier darf A nicht S0 sein.
//			c) Wenn [S0 → S.] in Ii, setze ACTION[i,$] auf "akzeptieren"
//			Ergeben sich aus diesen Regeln Konflikte, sprechen wir davon, dass es sich nicht um eine SLR(1)-Grammatik handelt. 
//			In diesem Fall erstellt der Algorithmus keinen Parser.
//		3. Die GOTO-Übergänge für Zustand i werden für alle Nichtterminale A nach folgender Regel konstruiert: 
//			Wenn GOTO(Ii,A)=Ij, dann GOTO(i,A)=j.
//		4. Alle nicht durch die Regeln (2) und (3) definierten Einträge werden auf "Fehler" gesetzt.
//		5. Der Ausgangszustand des Parsers ist derjenige, der aus der Item-Menge konstruiert wurde, die [S0 → .S] enthält.
				
		Map<Set<LR0Item>, State> itemSet2State=new HashMap<Set<LR0Item>, State>();
		Set<NonTerminalSymbol> allNonTerminalSymbols = getGrammar().getAllNonTerminals();
		
		Production startProduction=getGrammar().getStartProduction(); // S0 → S
		LR0Item startItem=new LR0Item(startProduction, 0); // [S0 → .S] -- Zum Erkennen des Startzustands
		LR0Item acceptanceItem=new LR0Item(startProduction, startProduction.getRhs().size()); // [S0 → S.] -- Zum Erkennen des akzeptierenden Zustands
	
		ParseTable parseTable=new ParseTable();
		// 1. Konstruieren Sie C={I0, I1, ..., In}, die Sammlung der LR(0)-Item-Mengen für G0
		List<Set<LR0Item>> cannonicalCollectionOfLR0Items = cannonicalCollectionOfLR0Items();
		
		// 2. Der Zustand i wird aus Ii erstellt. 
		for (int i = 0; i < cannonicalCollectionOfLR0Items.size(); i++) {
			Set<LR0Item> anItemSet = cannonicalCollectionOfLR0Items.get(i);
			State aState=new State(i);
			itemSet2State.put(anItemSet, aState);
			// 5. Der Ausgangszustand des Parsers ist derjenige, der aus der Item-Menge konstruiert wurde, die [S0 → .S] enthält.
			if (anItemSet.contains(startItem)){
				parseTable.setInitialState(aState);
			}
		}
		for (int i = 0; i < cannonicalCollectionOfLR0Items.size(); i++) {
			Set<LR0Item> anItemSet = cannonicalCollectionOfLR0Items.get(i);
			State aState=itemSet2State.get(anItemSet);
			for (LR0Item anItem : anItemSet) {
				// 2a) Wenn [A → α.aβ] in Ii und GOTO(Ii,a) = Ij, dann setze ACTION[i,a] auf "shift j". Hier muss a ein Terminal sein.
				if (anItem.getNextSymbol()!=null && anItem.getNextSymbol() instanceof TerminalSymbol){					
					TerminalSymbol nextSymbol=(TerminalSymbol) anItem.getNextSymbol();					
					Set<LR0Item> targetItemSet=gotoSet(anItemSet, nextSymbol);
					if (targetItemSet.size()>0){
						State targetState=itemSet2State.get(targetItemSet);
						Action shiftAction=new ShiftAction(targetState);
						Action existingAction=parseTable.getAction(aState, nextSymbol);
						if (existingAction!=null && !(existingAction instanceof ErrorAction) && !existingAction.equals(shiftAction)){ // Konflikt erkannt
							// Beim Dangling-Else-Problem wird ein shift-reduce-Konflikt zugunsten des shift aufgelöst (Vgl. Drachenbuch S. 336f)
							if (ELSE.equals(nextSymbol.getName()) && existingAction instanceof ReduceAction){
								logger.info("Resolve Dangling-Else Shift-Reduce-Conflict for the benefit of Shift: State="+aState+", symbol="+nextSymbol+
										", shiftAction="+shiftAction+", existingAction="+existingAction);
							}
							else{
								throw new InvalidGrammarException("No SLR(1) Grammar due to action table conflict: State="+aState+", symbol="+nextSymbol+
										", shiftAction="+shiftAction+", existingAction="+existingAction);
							}
						}
						parseTable.getActionTableForState(aState).setActionForTerminalSymbol(shiftAction, nextSymbol);
					}
				}
				// 2b) Wenn [A → α.] in Ii, setze ACTION[i,a] für alle a in FOLLOW(A) auf "reduce A → α". Hier darf A nicht S0 sein.
				if (anItem.getNextSymbol()==null && !anItem.getProduction().getLhs().equals(getGrammar().getStartSymbol())){
					NonTerminalSymbol lhs = anItem.getProduction().getLhs();					
					Action reduceAction=new ReduceAction(anItem.getProduction());
					Set<TerminalSymbol> followers = getGrammar().follow(lhs);
					for (TerminalSymbol aTerminalSymbol : followers) {
						boolean takeReduceAction=true;
						Action existingAction=parseTable.getAction(aState, aTerminalSymbol);
						if (existingAction!=null && !(existingAction instanceof ErrorAction) && !existingAction.equals(reduceAction)){ // Konflikt erkannt
							// Beim Dangling-Else-Problem wird ein shift-reduce-Konflikt zugunsten des shift aufgelöst (Vgl. Drachenbuch S. 336f)
							if (ELSE.equals(aTerminalSymbol.getName()) && existingAction instanceof ShiftAction){
								// Existierende ShiftAction wird einfach nicht durch die ReduceAction ersetzt
								takeReduceAction=false;
								logger.info("Ignoring Dangling-Else Shift-Reduce-Conflict: State="+aState+", symbol="+aTerminalSymbol+
										", reduceAction="+reduceAction+", existingAction="+existingAction);
							}
							else{
								throw new InvalidGrammarException("No SLR(1) Grammar due to action table conflict: State="+aState+", symbol="+aTerminalSymbol+
										", reduceAction="+reduceAction+", existingAction="+existingAction);
							}
						}
						if (takeReduceAction){
							parseTable.getActionTableForState(aState).setActionForTerminalSymbol(reduceAction, aTerminalSymbol);
						}
					}
				}
				// 2c) Wenn [S0 → S.] in Ii, setze ACTION[i,$] auf "akzeptieren"				
				if (anItemSet.contains(acceptanceItem)){
					Action acceptAction=new AcceptAction();
					Action existingAction=parseTable.getAction(aState, Grammar.INPUT_ENDMARKER);
					if (existingAction!=null && !(existingAction instanceof ErrorAction) && !existingAction.equals(acceptAction)){ // Konflikt erkannt
						throw new InvalidGrammarException("No SLR(1) Grammar due to action table conflict: State="+aState+", symbol="+Grammar.INPUT_ENDMARKER+
								", acceptAction="+acceptAction+", existingAction="+existingAction);
					}					
					parseTable.getActionTableForState(aState).setActionForTerminalSymbol(acceptAction, Grammar.INPUT_ENDMARKER);					
				}				
				// 3. Wenn GOTO(Ii,A)=Ij, dann GOTO(i,A)=j. (für alle Nichtterminale A)
				for (NonTerminalSymbol aNonTerminalSymbol : allNonTerminalSymbols) {
					Set<LR0Item> aGotoSet = gotoSet(anItemSet, aNonTerminalSymbol);
					if (aGotoSet.size()>0){
						State targetState=itemSet2State.get(aGotoSet);
						Goto aGoto=new Goto(targetState);
						Goto existingGoto=parseTable.getGoto(aState, aNonTerminalSymbol);
						if (existingGoto!=null && ! existingGoto.equals(aGoto)){ // Konflikt erkannt
							throw new InvalidGrammarException("No SLR(1) Grammar due to goto table conflict: State="+aState+", symbol="+aNonTerminalSymbol+
									", goto="+aGoto+", existingGoto="+existingGoto);
						}
						parseTable.getGotoTableForState(aState).setGotoForNonTerminalSymbol(aGoto, aNonTerminalSymbol);
					}
				}
			}
			// 4. Alle nicht durch die Regeln (2) und (3) definierten Einträge werden auf "Fehler" gesetzt.
			// 	Hier ist nichts zu tun, da ParseTable so implementiert ist, dass bei einem fehlenden Eintrag 
			// 	automatisch eine ErrorAction zurückgegeben wird. 
			
		}				
		return parseTable;
	}
	 
	/**
	 *Vgl. Drachenbuch: Abbildung 4.32
	 *
	 */
	public Set<LR0Item> closure(Set<LR0Item> items) {
//		Algorithmus aus Drachenbuch: Abbildung 4.32
//		SetOfItems CLOSURE(I) {
//			J=I;
//			repeat
//				for ( jedes Item A → α.Bβ in J )
//					for ( jede Produktion B → γ von G)
//						if (B → .γ ist nicht in J)
//							füge B → .γ zu J hinzu
//			until keine Items mehr in einer Runde zu J hinzugefügt wurden
//			return J;
//		}
		List<LR0Item> result=new ArrayList<LR0Item>(items);
		boolean added= false;

		do{
			added=false;
			for (int i = 0; i < result.size(); i++) {
				LR0Item anItem = result.get(i);
				Symbol nextSymbol = anItem.getNextSymbol();
				if(nextSymbol == null) 
					continue;
				else if(nextSymbol instanceof TerminalSymbol)
					continue;
				List<Production> productions = getGrammar().getProductionsByLhs((NonTerminalSymbol) nextSymbol);
				for(Production aProduction: productions){
					LR0Item itemCandidate = new LR0Item(aProduction, 0);
					if(!result.contains(itemCandidate)){
						result.add(itemCandidate);
						added = true;
					}
				}
			}
		}
		while(added);
		return new HashSet<LR0Item>(result);
	}
	 	
	/**
	 * Berechnet die Hülle von Items, die unmittelbar nach dem Lesen eines gegebenen 
	 * Symbols aus einer gegebenen Item-Menge folgen.
	 * Vgl. Drachembuch: S.296/ Beispiel 4.27
	 * 
	 * @param items Item-Menge, zu der für jedes Element ein "Folge-Item" gesucht wird.
	 * @param s oberstes Stack-Symbol
	 * @return Hülle der gefundenen "Folge"-Item-Menge
	 */
	public Set<LR0Item> gotoSet(Set<LR0Item> items, Symbol s) {
		// Algorithmus für LR(1) aus Drachenbuch (eng): Abbildung 4.40
		//	SetOfItems GOTO(I,X) {
		//		J=leere Menge;
		// 		for ( jedes Item [A → α.Xβ,a] in I )
		// 			füge Item [A → αX.β,a] zu J hinzu;
		// 		return CLOSURE(J);
		//	}
		// --> funktioniert analog für LR(0) Items
		
		Set<LR0Item> J = new HashSet<LR0Item>();
		for(LR0Item item : items) {
			if(s.equals(item.getNextSymbol())){ // nach Punkt soll Symbol s folgen
				LR0Item newItem = new LR0Item(item.getProduction(), (item.getIndex()+1));
				
				// da nur aus geg. Set (items) neue Items mit verschobenem Punkt erstellt werden,
				// können keine doppelten Elemente in Set J auftreten
				J.add(newItem); 
			}
		}
		
		return closure(J);
	}
	 
	/**
	 *Vgl. Drachembuch: Abbildung 4.33
	 */
	public List<Set<LR0Item>> cannonicalCollectionOfLR0Items() {
//		Drachembuch: Abbildung 4.33
//		void items(G0){
//			C = {CLOSURE({[S0 --> .S]})};
//			repeat
//				for ( jede Item-Menge I in C )
//					for ( jedes Grammatiksymbol X )
//						if ( GOTO(I,X) ist nicht leer und nicht in C )
//							füge GOTO(I,X) zu C hinzu;
//			until es werden keine neuen Item-Mengen mehr in einer Runde zu C hinzugefügt
		
		List<Production> startProductions=this.getGrammar().getProductionsByLhs(getGrammar().getStartSymbol());
		if (startProductions.size()==0){
			throw new IllegalStateException("cannonicalCollectionOfLR0Items failed to determine start production");
		}
		if (startProductions.size()>1){
			throw new IllegalStateException("cannonicalCollectionOfLR0Items failed to determine unique start production");
		}
		
		List<Symbol> allSymbols=getGrammar().getAllSymbols();
		
		Set<LR0Item> startSet=new HashSet<LR0Item>();
		startSet.add(new LR0Item(startProductions.get(0), 0)); // [S0 --> .S]
		List<Set<LR0Item>> cannonicalCollection= new ArrayList<Set<LR0Item>>(); // C
		cannonicalCollection.add(closure(startSet)); // C = {CLOSURE({[S0 --> .S]})};
		boolean anyItemSetAdded=false;
		do{
			anyItemSetAdded=false;
			// Achtung: cannonicalCollection muss über den Index iteriert werden, weil Itemmengen hinzugefügt werden
			for (int i = 0; i < cannonicalCollection.size(); i++) { // for ( jede Item-Menge I in C )				
				Set<LR0Item> anItemSet = cannonicalCollection.get(i);
				for (Symbol aSymbol : allSymbols) { // for ( jedes Grammatiksymbol X )
					Set<LR0Item> aGotoSet=gotoSet(anItemSet, aSymbol);
					if (!aGotoSet.isEmpty() && !cannonicalCollection.contains(aGotoSet)){ // if ( GOTO(I,X) ist nicht leer und nicht in C )
						cannonicalCollection.add(aGotoSet);// füge GOTO(I,X) zu C hinzu;
						anyItemSetAdded=true;
					}
				}
			}			
		}
		while(anyItemSetAdded); // es werden keine neuen Item-Mengen mehr in einer Runde zu C hinzugefügt
		
		return cannonicalCollection;
		
	}
	 
}
 
