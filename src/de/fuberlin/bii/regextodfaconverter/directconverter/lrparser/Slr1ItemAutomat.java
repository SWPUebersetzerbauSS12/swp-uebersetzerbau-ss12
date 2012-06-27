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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.fuberlin.bii.regextodfaconverter.directconverter.AutomatEventHandler;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ContextFreeGrammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElement;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.TerminalSet;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminator;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr0Closure;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr0Item;
import de.fuberlin.bii.utils.Notification;
import de.fuberlin.bii.utils.Test;

/**
 * Ein LR(0)-Parser. Ein LR(0)-Parser arbeitet grundsätzlich ohne Lookahead.
 * Dieser LR(0)-Parser reduziert zudem die Wahrscheinlichkeit für das Auftreten
 * von shift-reduce- und reduce-reduce-Konflikten, indem die Followmengen bei
 * der Erzeugung der reduce-Eintrage betrachtet werden. Kommt es so zu keinen
 * Konflikten, dann liefert die Eigenschaftsabfrage mit
 * {@link ItemAutomata#isReduceConflictFree()} den Wert true.
 * 
 * @author Johannes Dahlke
 * 
 * @param <Element>
 *            der Typ eines Elementes der zu verarbeitenden Eingabe.
 */
public class Slr1ItemAutomat<Element extends Symbol> extends
		Lr0ItemAutomat<Element> {

	public Slr1ItemAutomat(ContextFreeGrammar grammar) {
		super(grammar);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void SetupParserTable(Lr0Closure startClosure) {

		if (readPersistenParserTable()) {
			return;
		}

		HashSet<Lr0Closure> unhandledClosures = new HashSet<Lr0Closure>() {

			@Override
			public boolean contains(Object o) {
				for (Lr0Closure closure : this) {
					if (closure.equals((Lr0Closure) o))
						return true;
				}
				return false;
			}
		};

		int closureCounter = 0;

		startClosure.setNumber(closureCounter++);
		unhandledClosures.add(startClosure);

		HashMap<Nonterminal, TerminalSet> followSets = grammar.getFollowSets();
		RuleElement nextRuleElement;

		while (unhandledClosures.size() > 0) {
			Lr0Closure currentClosure = unhandledClosures.iterator().next();

			Map<RuleElement, AutomatEventHandler> handlerMap = new HandlerMap();

			for (Lr0Item item : currentClosure.getItemSet()) {

				nextRuleElement = item.peekNextRuleElement();
				if (Test.isAssigned(nextRuleElement)) {
					if (nextRuleElement instanceof Terminal) {
						// add shift actions
						Lr0Closure toClosure = gotoNextClosure(currentClosure,
								nextRuleElement, this.grammar);
						if (!parserTable.containsKey(toClosure)
								&& !unhandledClosures.contains(toClosure)) {
							toClosure.setNumber(closureCounter++);
							unhandledClosures.add(toClosure);
						}
						ShiftAction<Element, Lr0Closure> shiftAction = new ShiftAction<Element, Lr0Closure>(
								toClosure, (Terminal<Element>) nextRuleElement);

						if ((handlerMap.containsKey(nextRuleElement) && !handlerMap
								.get(nextRuleElement).equals(shiftAction))) {
							isReduceConflictFree = false;
							// Conflict happen. Store the conflicting action as
							// alternative for later error handling
							if (handlerMap.get(nextRuleElement) instanceof Action) {
								Action seniorAction = shiftAction;
								seniorAction.addAlternative((Action) handlerMap
										.get(nextRuleElement));
								handlerMap.put(nextRuleElement, seniorAction);
							}
						} else {
							// otherwise there is not yet defined an action. So
							// we add one
							handlerMap.put(nextRuleElement, shiftAction);
						}

					} else if (nextRuleElement instanceof Nonterminal) {
						// add goto's
						Lr0Closure toClosure = gotoNextClosure(currentClosure,
								nextRuleElement, this.grammar);
						if (!parserTable.containsKey(toClosure)
								&& !unhandledClosures.contains(toClosure)) {
							toClosure.setNumber(closureCounter++);
							unhandledClosures.add(toClosure);
						}
						Goto gotoHandler = new Goto(toClosure,
								(Nonterminal) nextRuleElement);
						// check for multiples with gotos not necessary
						handlerMap.put(nextRuleElement, gotoHandler);
					}
				} else {
					if (item.toProduction().equals(startProduction))
						// add accept action
						handlerMap.put(new Terminator(), new AcceptAction());
					else {
						// add reduce actions
						ProductionRule reduceProduction = item.toProduction();
						Nonterminal nonterminal = reduceProduction
								.getLeftRuleSide();
						Set<Terminal> followSet = followSets.get(nonterminal);
						ReduceAction<Element, Lr0Closure> reduceAction = new ReduceAction<Element, Lr0Closure>(
								reduceProduction);
						for (Terminal<Element> terminal : followSet) {
							if ((handlerMap.containsKey(terminal) && !handlerMap
									.get(terminal).equals(reduceAction))) {
								isReduceConflictFree = false;
								// Conflict happen. Store the conflicting action
								// as alternative for later error handling
								if (handlerMap.get(terminal) instanceof Action) {
									Action seniorAction = (Action) handlerMap
											.get(terminal);
									seniorAction.addAlternative(reduceAction);
								}
							} else {
								// otherwise there is not yet defined an action.
								// So we add one
								handlerMap.put(terminal, reduceAction);
							}

						}
					}
				}
			}
			unhandledClosures.remove(currentClosure);

			parserTable.put(currentClosure, handlerMap);
		}

		writePersistentParserTable();
	}

	private boolean readPersistenParserTable() {
		File dir = new File("/tmp/lexergen/");
		File parserTableObject = new File("/tmp/lexergen/parserTable");
		long start;
		long end;
		long length;

		if (dir.exists() && dir.isDirectory() && parserTableObject.exists()) {
			try {
				FileInputStream fInp = new FileInputStream(parserTableObject);
				ObjectInputStream inp = new ObjectInputStream(fInp);

				// benchmark reading process
				start = System.currentTimeMillis();

				Object o = inp.readObject();

				end = System.currentTimeMillis();
				length = (end - start);
				Notification.printDebugInfoMessage("Read parser table in "
						+ (length / 1000) + "." + (length % 1000) + "s");

				this.parserTable = (Map<Lr0Closure, Map<RuleElement, AutomatEventHandler>>) o;
				return true;
			} catch (Exception e) {
				Notification
						.printErrorMessage("could not find or read parser table object");
				e.printStackTrace();
			}
		}
		return false;
	}

	private void writePersistentParserTable() {

		File dir = new File("/tmp/lexergen/");
		File parserTableObject = new File("/tmp/lexergen/parserTable");

		dir.mkdirs();
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(
					new FileOutputStream(parserTableObject));

			out.writeObject(parserTable);

		} catch (Exception e) {
			Notification
					.printErrorMessage("problems with writing back parser table");
			parserTableObject.delete();
			e.printStackTrace();
		}

	}
}
