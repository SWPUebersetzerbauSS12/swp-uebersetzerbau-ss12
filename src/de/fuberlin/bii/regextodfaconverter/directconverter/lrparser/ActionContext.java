package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElement;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Closure;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr1Closure;


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
