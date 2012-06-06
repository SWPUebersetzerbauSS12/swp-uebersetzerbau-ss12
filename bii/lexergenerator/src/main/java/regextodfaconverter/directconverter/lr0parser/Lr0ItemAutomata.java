/*
 * 
 * Copyright 2012 lexergen.
 * This file is part of lexergen.
 * 
 * lexergen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * lexergen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with lexergen.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * lexergen:
 * A tool to chunk source code into tokens for further processing in a compiler chain.
 * 
 * Projectgroup: bi, bii
 * 
 * Authors: Johannes Dahlke
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package regextodfaconverter.directconverter.lr0parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionMap;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElementSequenz;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminator;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;
import regextodfaconverter.directconverter.lr0parser.itemset.Item;
import regextodfaconverter.directconverter.lr0parser.itemset.ItemSet;
import utils.Notification;
import utils.Test;

/**
 * Ein LR(0)-Parser. Ein LR(0)-Parser arbeitet grundsätzlich ohne Lookahead. 
 * Dieser LR(0)-Parser reduziert zudem die Wahrscheinlichkeit für das Auftreten 
 * von shift-reduce- und reduce-reduce-Konflikten, indem die Followmengen bei 
 * der Erzeugung der reduce-Eintrage betrachtet werden. Kommt es so zu keinen Konflikten, 
 * dann liefert die Eigenschaftsabfrage mit {@link Lr0ItemAutomata#isSLR1()} den Wert true.   
 * 
 * @author Johannes Dahlke
 *
 * @param <Element> der Typ eines Elementes der zu verarbeitenden Eingabe.
 */
public class Lr0ItemAutomata<Element extends Comparable<Element>> implements ItemAutomata<Element>, ItemAutomataInterior<Element> {

	private HashSet<Closure> closures = new HashSet<Closure>();
	private Closure currentClosure = null;
	private ContextFreeGrammar grammar;
	private ProductionRule startProduction;

	private Stack<RuleElement> symbolStack;
	private Stack<Closure> closureStack;
	private Queue<Element> inputQueue;
	
	private ReduceEventHandler reduceEventHandler;
	private ShiftEventHandler shiftEventHandler;
	

	private Map<Closure, Map<RuleElement, EventHandler>> parserTable = new HashMap<Closure, Map<RuleElement, EventHandler>>() {

		@Override
		public boolean containsKey( Object o) {
			for ( Closure closure : this.keySet()) {
				if ( closure.equals( (Closure) o))
					return true;
			}
			return false;
		};


		@Override
		public Map<RuleElement, EventHandler> get( Object key) {
			for ( Closure closure : this.keySet()) {
				if ( closure.equals( key))
					return super.get( closure);
			}
			return null;
		}

	};
	
	private boolean isSLR1 = true;


	public Lr0ItemAutomata( ContextFreeGrammar grammar) {
		super();
		this.grammar = grammar;
		InitializeAutomata();
	}


	private Closure calcStartClosure() {
		Nonterminal startSymbol = grammar.getStartSymbol();
		Item startItem = new Item( new Nonterminal( startSymbol.toString() + "'"), startSymbol);
		startProduction = startItem.toProduction();
		return calcClosureForStartItem( startItem, grammar);
	}


	private void InitializeAutomata() {
		Closure startClosure = calcStartClosure();
		closures.add( startClosure);
		currentClosure = startClosure;

		SetupParserTable( startClosure);

		InitializeStacks();
	}


	private void SetupParserTable( Closure startClosure) {
		HashSet<Closure> unhandledClosures = new HashSet<Closure>() {

			@Override
			public boolean contains( Object o) {
				for ( Closure closure : this) {
					if ( closure.equals( (Closure) o))
						return true;
				}
				return false;
			}
		};
		int closureCounter = 0;

		startClosure.setNumber( closureCounter++);
		unhandledClosures.add( startClosure);

		HashMap<Nonterminal, Set<Terminal>> followSets = grammar.getFollowSets();
		RuleElement nextRuleElement;

		while ( unhandledClosures.size() > 0) {
			Closure currentClosure = unhandledClosures.iterator().next();
			Map<RuleElement, EventHandler> handlerMap = new HashMap<RuleElement, EventHandler>() {

				@Override
				public boolean containsKey( Object key) {
					for ( RuleElement element : this.keySet()) {
						if ( element.equals( key))
							return true;
					}
					return false;
				}


				@Override
				public EventHandler get( Object key) {
					for ( RuleElement element : this.keySet()) {
						if ( element.equals( key))
							return super.get( element);
					}
					return null;
				}

			};
			for ( Item item : currentClosure.getItemSet()) {
				nextRuleElement = item.peekNextRuleElement();
				if ( Test.isAssigned( nextRuleElement)) {
					if ( nextRuleElement instanceof Terminal) {
						// add shift actions
						Closure toClosure = gotoNextClosure( currentClosure, nextRuleElement, this.grammar);
						if ( !parserTable.containsKey( toClosure) && !unhandledClosures.contains( toClosure)) {
							toClosure.setNumber( closureCounter++);
							unhandledClosures.add( toClosure);
						}
						ShiftAction<Element> shiftAction = new ShiftAction<Element>( this, toClosure, (Terminal<Element>) nextRuleElement);
						isSLR1 &= !handlerMap.containsKey( nextRuleElement);
			  		handlerMap.put( nextRuleElement, shiftAction);
					} else if ( nextRuleElement instanceof Nonterminal) {
						// add goto's
						Closure toClosure = gotoNextClosure( currentClosure, nextRuleElement, this.grammar);
						if ( !parserTable.containsKey( toClosure) && !unhandledClosures.contains( toClosure)) {
							toClosure.setNumber( closureCounter++);
							unhandledClosures.add( toClosure);
						}
						Goto gotoHandler = new Goto( this, toClosure, (Nonterminal) nextRuleElement);
						// check for multiples with gotos not necessary  
						handlerMap.put( nextRuleElement, gotoHandler);
					}
				} else {
					if ( item.toProduction().equals( startProduction))
						// add accept action
						handlerMap.put( new Terminator(), new AcceptAction( this));
					else {
						// add reduce actions
						ProductionRule reduceProduction = item.toProduction();
						Nonterminal nonterminal = reduceProduction.getLeftRuleSide();
						Set<Terminal> followSet = followSets.get( nonterminal);
						for ( Terminal<Element> terminal : followSet) {
							ReduceAction<Element> reduceAction = new ReduceAction<Element>( this, reduceProduction);
							isSLR1 &= !handlerMap.containsKey( nextRuleElement);
							handlerMap.put( terminal, reduceAction);
						}
					}
				}
			}
			unhandledClosures.remove( currentClosure);

			parserTable.put( currentClosure, handlerMap);
		}
	}


	private void InitializeStacks() {
		symbolStack = new Stack<RuleElement>();
		symbolStack.add( new Terminator());
		closureStack = new Stack<Closure>();
		closureStack.add( currentClosure); // assert currentClosure == startClosure;
	}


	private void LoadInputIntoQueue( List<Element> input) {
		inputQueue = new ArrayBlockingQueue<Element>( input.size());
		for ( Element element : input) {
			inputQueue.offer( element);
		}
	}


	private void ResetAutomata() {
		closures = new HashSet<Closure>();
		currentClosure = null;
		isSLR1 = true;
		InitializeAutomata();
	}


	public boolean match( List<Element> input) {
		ResetAutomata();
		LoadInputIntoQueue( input);

	  EventHandler handler;
		boolean accepted = false;
		try {
		 do {	
  			// get next to handle
				currentClosure = closureStack.peek();
				
				// we peek the next input element 
				Terminal terminalToHandle = inputQueue.isEmpty() ? new Terminator() : new Terminal( inputQueue.peek());
				
				handler = parserTable.get( currentClosure).get( terminalToHandle);
				handler.handle( this);
			  if ( handler instanceof ReduceAction) {
			  	currentClosure = closureStack.peek();
			  	RuleElement nonterminalToHandle = symbolStack.peek();
			  	// process the goto
			  	assert nonterminalToHandle instanceof Nonterminal;
			  	EventHandler gotoHandler = parserTable.get( currentClosure).get( nonterminalToHandle);
			  	gotoHandler.handle( this);	
			  
			    // notify about reduce action
			  	if ( Test.isAssigned( reduceEventHandler)) {
			  	  reduceEventHandler.handle( this, ((ReduceAction) handler).getReduceRule());
			  	}
			  	
			  } else if ( handler instanceof ShiftAction) {			  	
			  	if ( Test.isAssigned( shiftEventHandler))
			  		shiftEventHandler.handle( this, (Terminal) terminalToHandle);
			  }
				
				accepted = handler instanceof AcceptAction;
			} while ( !accepted);
			
		} catch ( Exception e) {
			Notification.printDebugException( e);
			return false;
		}
		return true;
	}


	private static Closure calcClosureForStartItem( Item startItem, ContextFreeGrammar grammar) {
		ItemSet itemSet = new ItemSet();
		itemSet.add( startItem);
		Closure closure = calcClosureOfItemSet( itemSet, grammar);
		closure.putAsKernelItem( startItem);
		return closure;
	}


	private static Closure calcClosureOfItemSet( ItemSet itemSet, ContextFreeGrammar grammar) {
		Closure result = new Closure();
		for ( Item item : itemSet) {
			if ( item.getAnalysePosition() == 0)
				result.addAsNonkernelItem( item);
			else
				result.addAsKernelItem( item);
		}

		boolean hasClosureGrown;
		do {
			hasClosureGrown = false;
			Set<Item> currentItemSet = new HashSet<Item>( result.keySet());
			for ( Item item : currentItemSet) {
				RuleElement ruleElement = item.peekNextRuleElement();
				if ( Test.isAssigned( ruleElement) && ruleElement instanceof Nonterminal) {
					Nonterminal leftRuleSideCandidate = (Nonterminal) ruleElement;
					HashSet<RuleElementSequenz> setOfRightRules = grammar.get( ruleElement);
					if ( Test.isAssigned( setOfRightRules)) {
						for ( RuleElementSequenz rightRuleSide : setOfRightRules) {
							Item newItem = new Item( leftRuleSideCandidate, rightRuleSide);
							if ( !result.containsKey( newItem)) {
								result.addAsNonkernelItem( newItem);
								hasClosureGrown = true;
							}
						}
					}
				}
			}
		} while ( hasClosureGrown);

		return result;
	}


	/**
	 * Formal: GOTO( I,X) = CLOSURE( { [A -> aX.b] | [A -> a.Xb] \in I })
	 * 
	 * @param itemSet
	 * @return
	 */
	private static Closure gotoNextClosure( Closure fromClosure, RuleElement transitionElement, ContextFreeGrammar grammar) {
		ItemSet fromItemSet = fromClosure.getItemSet();
		ItemSet toItemSet = new ItemSet();
		for ( Item fromItem : fromItemSet) {
			if ( transitionElement.equals( fromItem.peekNextRuleElement()))
				toItemSet.add( new Item( fromItem, fromItem.getAnalysePosition() + 1));
		}
		return calcClosureOfItemSet( toItemSet, grammar);
	}


	public Stack<RuleElement> getSymbolStack() {
		return symbolStack;
	}


	public Stack<Closure> getClosureStack() {
		return closureStack;
	}


	public Queue getInputQueue() {
		return inputQueue;
	}


	@Override
	public String toString() {
		String result = "";
		List<Closure> closureList = new ArrayList<Closure>( parserTable.keySet());
		List<ProductionRule> productionList = new ArrayList<ProductionRule>( grammar.getProductions());

		RuleElement[] terminalElements = new RuleElement[grammar.getTerminals().size() + 1];
		terminalElements = grammar.getTerminals().toArray( terminalElements);
		terminalElements[grammar.getTerminals().size()] = new Terminator();
		Arrays.sort( terminalElements);

		RuleElement[] nonterminalElements = new RuleElement[grammar.getNonterminals().size()];
		nonterminalElements = grammar.getNonterminals().toArray( nonterminalElements);
		Arrays.sort( nonterminalElements);

		Collections.sort( closureList, new Comparator<Closure>() {

			public int compare( Closure c1, Closure c2) {
				return c1.getNumber().compareTo( c2.getNumber());
			}
		});

		for ( int i = 0; i < closureList.size(); i++) {
			Closure closure = closureList.get( i);
			Map<RuleElement, EventHandler> handlerMap = parserTable.get( closure);

			if ( closure.getName().isEmpty())
				closure.setNumber( closureList.indexOf( closure));

			result += closure.getName() + "\t";
			for ( RuleElement ruleElement : terminalElements) {
				EventHandler handler = handlerMap.get( ruleElement);
				if ( handler instanceof ShiftAction) {
					result += " \t " + ruleElement + " : s" + closureList.indexOf( ( (ShiftAction) handler).getToClosure());
				} else if ( handler instanceof ReduceAction) {
					result += " \t " + ruleElement + " : r" + productionList.indexOf( ( (ReduceAction) handler).getReduceRule());
				} else if ( handler instanceof AcceptAction) {
					result += " \t " + ruleElement + " : acc";
				} else {
					result += " \t\t ";
				}
			}

			for ( RuleElement ruleElement : nonterminalElements) {
				EventHandler handler = handlerMap.get( ruleElement);
				if ( handler instanceof Goto) {
					result += " \t " + ruleElement + " : goto " + closureList.indexOf( ( (Goto) handler).getToClosure());
				} else {
					result += " \t\t ";
				}
			}
			result += "\n";
		}
		return result;
	}


	public ContextFreeGrammar getGrammar() {
		return grammar;
	}


	public boolean isSLR1() {
		return isSLR1;
	}
	

	public void setReduceEventHandler( ReduceEventHandler reduceEventHandler) {
		this.reduceEventHandler = reduceEventHandler;
	}


	public void setShiftEventHandler( ShiftEventHandler shiftEventHandler) {
		this.shiftEventHandler = shiftEventHandler;
	}
}
