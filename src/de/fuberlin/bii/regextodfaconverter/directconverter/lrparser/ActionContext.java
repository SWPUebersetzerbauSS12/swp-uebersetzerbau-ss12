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

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElement;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Closure;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr1Closure;

/**
 * In einem ActionContext wird ein Schnappschuss der {@link ItemAutomat}-Umgebung zur Ausführungszeit der Aktion für eine gegebenenfalls spätere Wiederherstellung hinterlegt.  
 * 
 * @author Johannes Dahlke
 *
 * @param <Element>
 * @param <SpecializedClosure>
 */
public class ActionContext<Element extends Symbol, SpecializedClosure extends Closure> {
	
	private SpecializedClosure currentClosure;
	
	private Stack<RuleElement> symbolStack = new Stack<RuleElement>();
	private Stack<SpecializedClosure> closureStack = new Stack<SpecializedClosure>();
	private Queue<Element> inputQueue;
	
	private int sequenceNumber;
	private Action action;
	
	
	public ActionContext( Action action, 	int sequenceNumber, ItemAutomatInterior<Element, SpecializedClosure> itemAutomat, SpecializedClosure currentClosure) {
		super();
		this.action = action;
		this.sequenceNumber = sequenceNumber;
		shallowCopyStack( itemAutomat.getClosureStack(), closureStack);
		shallowCopyStack( itemAutomat.getSymbolStack(), symbolStack);
	  inputQueue = shallowCopyQueue( itemAutomat.getInputQueue());	
		this.currentClosure = currentClosure;
	}
	
	
	public static <Element extends Symbol> Queue<Element> shallowCopyQueue( Queue<Element> inputQueue) {
		Queue<Element> result = new ArrayBlockingQueue<Element>( Math.max( inputQueue.size(),1));
		for ( Element element : inputQueue) {
			result.add( element);
		}
		return result;
	}


	public static void shallowCopyStack( Stack fromStack, Stack toStack) {
		toStack.clear();
		for ( Object fromObject : fromStack) {
		  toStack.push( fromObject);
		}
	}


	public ItemAutomatInterior<Element, SpecializedClosure> getItemAutomat() {
		return new ItemAutomatInterior<Element, SpecializedClosure>() {

			public Stack<RuleElement> getSymbolStack() {
				return symbolStack;
			}

			public Stack<SpecializedClosure> getClosureStack() {
				return closureStack;
			}

			public Queue<Element> getInputQueue() {
				return inputQueue;
			}
		};
	}
	
	
	public SpecializedClosure getCurrentClosure() {
		return currentClosure;
	}
	
	
	public Action getAction() {
		return action;
	}
	
	
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	
}
