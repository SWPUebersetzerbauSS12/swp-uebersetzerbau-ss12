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

package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser;

import java.io.Serializable;
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

import de.fuberlin.bii.regextodfaconverter.directconverter.AutomatEventHandler;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ContextFreeGrammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.EmptyString;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionMap;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElement;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElementSequenz;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.TerminalSet;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminator;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Closure;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr0Closure;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr0ItemSet;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr0Item;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr1Closure;
import de.fuberlin.bii.utils.Notification;
import de.fuberlin.bii.utils.Test;

/**
 * Ein LR(0)-Parser. Ein LR(0)-Parser arbeitet grundsätzlich ohne Lookahead. 
 * 
 * @author Johannes Dahlke
 *
 * @param <Element> der Typ eines Elementes der zu verarbeitenden Eingabe.
 */
public class Lr0ItemAutomat<Element extends Symbol> implements ItemAutomat<Element>, ItemAutomatInterior<Element, Lr0Closure>, Serializable {

	private static final long serialVersionUID = 5228756905357704645L;
	private HashSet<Lr0Closure> closures = new HashSet<Lr0Closure>();
	private Lr0Closure currentClosure = null;
	protected ContextFreeGrammar grammar;
	protected ProductionRule startProduction;

	private Stack<RuleElement> symbolStack;
	private Stack<Lr0Closure> closureStack;
	private Queue<Element> inputQueue;
	
	private ReduceEventHandler reduceEventHandler;
	private ShiftEventHandler shiftEventHandler;
	
	private Lr0Closure startClosure;
	
	protected boolean isReduceConflictFree = true;
	

	protected Map<Lr0Closure, Map<RuleElement, AutomatEventHandler>> parserTable = new HashMap<Lr0Closure, Map<RuleElement, AutomatEventHandler>>() {

		@Override
		public boolean containsKey( Object o) {
			for ( Lr0Closure closure : this.keySet()) {
				if ( closure.equals( (Lr0Closure) o))
					return true;
			}
			return false;
		};


		@Override
		public Map<RuleElement, AutomatEventHandler> get( Object key) {
			for ( Lr0Closure closure : this.keySet()) {
				if ( closure.equals( key))
					return super.get( closure);
			}
			return null;
		}

	};
	


	public Lr0ItemAutomat( ContextFreeGrammar grammar) {
		super();
		this.grammar = grammar;
		InitializeAutomat();
	}


	private Lr0Closure calcStartClosure() {
		Nonterminal startSymbol = grammar.getStartSymbol();
		Lr0Item startItem = new Lr0Item( new Nonterminal( startSymbol.toString() + "'"), startSymbol);
		startProduction = startItem.toProduction();
		return calcClosureForStartItem( startItem, grammar);
	}


	private void InitializeAutomat() {
		startClosure = calcStartClosure();
		closures.add( startClosure);
		currentClosure = startClosure;

		SetupParserTable( startClosure);

		InitializeStacks();
	}


	protected void SetupParserTable( Lr0Closure startClosure) {
		HashSet<Lr0Closure> unhandledClosures = new HashSet<Lr0Closure>() {

			@Override
			public boolean contains( Object o) {
				for ( Lr0Closure closure : this) {
					if ( closure.equals( (Lr0Closure) o))
						return true;
				}
				return false;
			}
		};
		int closureCounter = 0;

		startClosure.setNumber( closureCounter++);
		unhandledClosures.add( startClosure);

		HashMap<Nonterminal, TerminalSet> followSets = grammar.getFollowSets();
		RuleElement nextRuleElement;

		while ( unhandledClosures.size() > 0) {
			Lr0Closure currentClosure = unhandledClosures.iterator().next();
			Map<RuleElement, AutomatEventHandler> handlerMap = new HandlerMap();
			
			for ( Lr0Item item : currentClosure.getItemSet()) {
				nextRuleElement = item.peekNextRuleElement();
				if ( Test.isAssigned( nextRuleElement)) {
					if ( nextRuleElement instanceof Terminal) {
						// add shift actions
						Lr0Closure toClosure = gotoNextClosure( currentClosure, nextRuleElement, this.grammar);
						if ( !parserTable.containsKey( toClosure) && !unhandledClosures.contains( toClosure)) {
							toClosure.setNumber( closureCounter++);
							unhandledClosures.add( toClosure);
						}
					
						ShiftAction<Element, Lr0Closure> shiftAction = new ShiftAction<Element, Lr0Closure>( toClosure, (Terminal<Element>) nextRuleElement);
						
						if ((handlerMap.containsKey( nextRuleElement) 
							  && !handlerMap.get( nextRuleElement).equals( shiftAction))) {
							isReduceConflictFree = false;
							// Conflict happen. Store the conflicting action as alternative for later error handling
							if ( handlerMap.get( nextRuleElement) instanceof Action) {
							  Action seniorAction = shiftAction;
							  seniorAction.addAlternative( (Action) handlerMap.get( nextRuleElement));
							  handlerMap.put( nextRuleElement, seniorAction);
							}
						} else {
						  // otherwise there is not yet defined an action. So we add one
							handlerMap.put( nextRuleElement, shiftAction);
						}
						
					} else if ( nextRuleElement instanceof Nonterminal) {
						// add goto's
						Lr0Closure toClosure = gotoNextClosure( currentClosure, nextRuleElement, this.grammar);
						if ( !parserTable.containsKey( toClosure) && !unhandledClosures.contains( toClosure)) {
							toClosure.setNumber( closureCounter++);
							unhandledClosures.add( toClosure);
						}
						Goto gotoHandler = new Goto( toClosure, (Nonterminal) nextRuleElement);
						// check for multiples with gotos not necessary  
						handlerMap.put( nextRuleElement, gotoHandler);
					}
				} else {
					if ( item.toProduction().equals( startProduction))
						// add accept action
						handlerMap.put( new Terminator(), new AcceptAction());
					else {
						// add reduce actions
						ProductionRule reduceProduction = item.toProduction();
						ReduceAction<Element, Lr0Closure> reduceAction = new ReduceAction<Element, Lr0Closure>(reduceProduction);
						for ( Terminal<Element> terminal : grammar.getTerminals()) {
							if ((handlerMap.containsKey( terminal) 
								  && !handlerMap.get( terminal).equals( reduceAction))) {
								isReduceConflictFree = false;
								// Conflict happen. Store the conflicting action as alternative for later error handling
								if ( handlerMap.get( terminal) instanceof Action) {
								  Action seniorAction = (Action) handlerMap.get( terminal);
								  seniorAction.addAlternative( reduceAction);
								}
							} else {
							  // otherwise there is not yet defined an action. So we add one
								handlerMap.put( terminal, reduceAction);
							}
						}
						Terminator terminator = new Terminator();
						isReduceConflictFree &= !(handlerMap.containsKey( terminator) 
							  && !handlerMap.get( terminator).equals( reduceAction));
						handlerMap.put( terminator, reduceAction);
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
		closureStack = new Stack<Lr0Closure>();
		currentClosure = startClosure;
		closureStack.add( currentClosure); 
	}


	private void LoadInputIntoQueue( List<Element> input) {
		inputQueue = new ArrayBlockingQueue<Element>( input.size());
		for ( Element element : input) {
			inputQueue.offer( element);
		}
	}


	protected void ResetAutomat() {
		closures = new HashSet<Lr0Closure>();
		currentClosure = null;
		isReduceConflictFree = true;
		
		InitializeStacks();
	}


	public boolean match( List<Element> input) throws ItemAutomatException {
		ResetAutomat();
		LoadInputIntoQueue( input);

		int currentSequenceNumber = 0;
		Stack<ActionContext> alternativeActionStack = new Stack<ActionContext>();
	  AutomatEventHandler handler;
		boolean accepted = false;
		try {
		 do {	
  			// get next to handle
				currentClosure = closureStack.peek();
				
				// we peek the next input element 
				Terminal terminalToHandle = inputQueue.isEmpty() ? new Terminator() : new Terminal( inputQueue.peek());				
				handler = parserTable.get( currentClosure).get( terminalToHandle);
					

				alternativeAction: do {
					try {

						if ( handler instanceof Action
								&& ((Action)handler).hasAlternative()) {
							// put alternative on stack
							Action alternativeAction = ((Action) handler).getAlternative();
							ActionContext alternativeActionContext = new ActionContext( alternativeAction, currentSequenceNumber, this, currentClosure);
							alternativeActionStack.add( alternativeActionContext);
						}
						
						handler.handleOnAutomat( this);
					} catch ( Exception e) {
						if ( !alternativeActionStack.isEmpty()) {
							ActionContext<Element, Lr0Closure> alternativeActionContext = alternativeActionStack.pop();
							restoreFromContext( alternativeActionContext);
							currentSequenceNumber = alternativeActionContext.getSequenceNumber();
							currentClosure = alternativeActionContext.getCurrentClosure();
							handler = alternativeActionContext.getAction();
							continue alternativeAction; 
						} else {
							Notification.printDebugException( e);
							throw new Exception( "Cannot interpret symbol before " + inputQueue.toString());
						}
					}
					break;
				} while( true);
			  
				if ( handler instanceof ReduceAction) {
			  	currentClosure = closureStack.peek();
			  	RuleElement nonterminalToHandle = symbolStack.peek();
			  	// process the goto
			  	assert nonterminalToHandle instanceof Nonterminal;
			  	AutomatEventHandler gotoHandler = parserTable.get( currentClosure).get( nonterminalToHandle);
			  	gotoHandler.handleOnAutomat( this);	
			  
			    // notify about reduce action
			  	if ( Test.isAssigned( reduceEventHandler)) {
			  	  reduceEventHandler.handle( this, ((ReduceAction) handler).getReduceRule(), currentSequenceNumber);
			  	}
			  	
			  } else if ( handler instanceof ShiftAction) {			  	
			  	if ( Test.isAssigned( shiftEventHandler))
			  		shiftEventHandler.handle( this, (Terminal) terminalToHandle, currentSequenceNumber);
			  }
				
				currentSequenceNumber++;
				
				accepted = handler instanceof AcceptAction;
			} while ( !accepted);
			
		} catch ( Exception e) {
			Notification.printDebugException( e);
			throw new ItemAutomatException( e.getMessage()); 
			//return false;
		}
		return true;
	}
	
	private void restoreFromContext( ActionContext<Element, Lr0Closure> actionContext) {
		ActionContext.shallowCopyStack( actionContext.getItemAutomat().getClosureStack(), this.closureStack);
		ActionContext.shallowCopyStack( actionContext.getItemAutomat().getSymbolStack(), this.symbolStack);
	  this.inputQueue = ActionContext.shallowCopyQueue( actionContext.getItemAutomat().getInputQueue());	
	}


	private static Lr0Closure calcClosureForStartItem( Lr0Item startItem, ContextFreeGrammar grammar) {
		Lr0ItemSet itemSet = new Lr0ItemSet();
		itemSet.add( startItem);
		Lr0Closure closure = calcClosureOfItemSet( itemSet, grammar);
		closure.putAsKernelItem( startItem);
		return closure;
	}


	private static Lr0Closure calcClosureOfItemSet( Lr0ItemSet itemSet, ContextFreeGrammar grammar) {
		Lr0Closure result = new Lr0Closure();
		// J = I @see Drachenbuch S. 295
		for ( Lr0Item item : itemSet) {
			if ( item.getAnalysePosition() == 0)
				result.addAsNonkernelItem( item);
			else
				result.addAsKernelItem( item);
		}

		boolean hasClosureGrown;
		do {
			hasClosureGrown = false;
			Lr0ItemSet currentItemSet = new Lr0ItemSet( result.keySet());
			for ( Lr0Item item : currentItemSet) {
				RuleElement ruleElement = item.peekNextRuleElement();
				if ( Test.isAssigned( ruleElement) && ruleElement instanceof Nonterminal) {
					Nonterminal leftRuleSideCandidate = (Nonterminal) ruleElement;
					HashSet<RuleElementSequenz> setOfRightRules = grammar.get( ruleElement);
					if ( Test.isAssigned( setOfRightRules)) {
						for ( RuleElementSequenz rightRuleSide : setOfRightRules) {
							Lr0Item newItem = new Lr0Item( leftRuleSideCandidate, rightRuleSide);
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
	protected static Lr0Closure gotoNextClosure( Lr0Closure fromClosure, RuleElement transitionElement, ContextFreeGrammar grammar) {
		Lr0ItemSet fromItemSet = fromClosure.getItemSet();
		Lr0ItemSet toItemSet = new Lr0ItemSet();
		for ( Lr0Item fromItem : fromItemSet) {
			if ( transitionElement.equals( fromItem.peekNextRuleElement()))
				toItemSet.add( new Lr0Item( fromItem, fromItem.getAnalysePosition() + 1));
		}
		return calcClosureOfItemSet( toItemSet, grammar);
	}


	public Stack<RuleElement> getSymbolStack() {
		return symbolStack;
	}


	public Stack<Lr0Closure> getClosureStack() {
		return closureStack;
	}


	public Queue getInputQueue() {
		return inputQueue;
	}


	@Override
	public String toString() {
		String result = "";
		List<Lr0Closure> closureList = new ArrayList<Lr0Closure>( parserTable.keySet());
		List<ProductionRule> productionList = new ArrayList<ProductionRule>( grammar.getProductions());

		RuleElement[] terminalElements = new RuleElement[grammar.getTerminals().size() + 1];
		terminalElements = grammar.getTerminals().toArray( terminalElements);
		terminalElements[grammar.getTerminals().size()] = new Terminator();
		Arrays.sort( terminalElements);

		RuleElement[] nonterminalElements = new RuleElement[grammar.getNonterminals().size()];
		nonterminalElements = grammar.getNonterminals().toArray( nonterminalElements);
		Arrays.sort( nonterminalElements);

		Collections.sort( closureList, new Comparator<Lr0Closure>() {

			public int compare( Lr0Closure c1, Lr0Closure c2) {
				return c1.getNumber().compareTo( c2.getNumber());
			}
		});

		for ( int i = 0; i < closureList.size(); i++) {
			Lr0Closure closure = closureList.get( i);
			Map<RuleElement, AutomatEventHandler> handlerMap = parserTable.get( closure);

			if ( closure.getName().isEmpty())
				closure.setNumber( closureList.indexOf( closure));

			result += closure.getName() + "\t";
			for ( RuleElement ruleElement : terminalElements) {
				AutomatEventHandler handler = handlerMap.get( ruleElement);
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
				AutomatEventHandler handler = handlerMap.get( ruleElement);
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


	public boolean isReduceConflictFree() {
		return isReduceConflictFree;
	}
	

	public void setReduceEventHandler( ReduceEventHandler reduceEventHandler) {
		this.reduceEventHandler = reduceEventHandler;
	}


	public void setShiftEventHandler( ShiftEventHandler shiftEventHandler) {
		this.shiftEventHandler = shiftEventHandler;
	}
}
