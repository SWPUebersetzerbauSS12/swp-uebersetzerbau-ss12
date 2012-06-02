package regextodfaconverter.directconverter.lr0parser;


import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;

public class ShiftAction<Element extends Comparable<Element>> extends Action<Element> implements EventHandler {

	private Closure toClosure;
	private Terminal<Element> terminalToHandle;

	public ShiftAction(ItemAutomataInterior<Element> itemAutomata, Closure toClosure, Terminal<Element> theTerminalToHandle) {
		super( itemAutomata);
		this.toClosure = toClosure;
		this.terminalToHandle = theTerminalToHandle;
	}

	public Object handle(Object sender) throws ShiftException {
		if (itemAutomata.getInputQueue().peek().equals(terminalToHandle.getSymbol())) {
			itemAutomata.getClosureStack().push(toClosure);
		  Element nextElement = itemAutomata.getInputQueue().poll();
		  itemAutomata.getSymbolStack().push( new Terminal( nextElement));
		} else
			throw new ShiftException(String.format("Missing terminal %s to handle at the top of the stack.", terminalToHandle));

		return toClosure;
	}
	
	public Closure getToClosure() {
		return toClosure;
	}
	
	public Terminal<Element> getTerminalToHandle() {
		return terminalToHandle;
	}
	
	@Override
	public String toString() {
		return "Shift to " + toClosure.toString() + " by " + terminalToHandle.toString();
	}

}
