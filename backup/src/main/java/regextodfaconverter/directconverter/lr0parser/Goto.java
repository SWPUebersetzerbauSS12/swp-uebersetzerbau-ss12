package regextodfaconverter.directconverter.lr0parser;


import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;

public class Goto<Element extends Comparable<Element>> implements EventHandler {

	private Closure toClosure;
	private Nonterminal nonterminalToHandle;
	protected ItemAutomataInterior<Element> itemAutomata;
	
	public Goto(ItemAutomataInterior<Element> itemAutomata, Closure toClosure, Nonterminal theNonterminalToHandle) {
		super();
		this.itemAutomata = itemAutomata;
		this.toClosure = toClosure;
		this.nonterminalToHandle = theNonterminalToHandle;
	}

	public Object handle(Object sender) throws GotoException {
		if (itemAutomata.getSymbolStack().peek().equals( nonterminalToHandle)) {
			itemAutomata.getClosureStack().push(toClosure);
		} else
			throw new GotoException(String.format("Missing expected nonterminal %s.", nonterminalToHandle));

		return toClosure;
	}
	
	public Nonterminal getNonterminalToHandle() {
		return nonterminalToHandle;
	}
	
	public Closure getToClosure() {
		return toClosure;
	}
	
	@Override
	public String toString() {
		return "Goto " + toClosure.toString() + " by " + nonterminalToHandle.toString();
	}

}
