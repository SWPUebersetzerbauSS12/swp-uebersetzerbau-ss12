package de.fuberlin.projectci.grammar;

import java.util.ArrayList;
import java.util.List;

public class Production {
 
	private NonTerminalSymbol lhs;
	private List<Symbol> rhs;
	
	public Production(NonTerminalSymbol lhs, List<?extends Symbol> rhs) {
		super();
		this.lhs = lhs;
		this.rhs = (List<Symbol>) rhs;	// TODO ist das so sinnvoll?
	}

	public Production(NonTerminalSymbol lhs, Symbol[] rhs) {
		super();
		this.lhs = lhs;
		this.rhs = new ArrayList<Symbol>();
		for (int i = 0; i < rhs.length; i++) {
			Symbol aSymbol = rhs[i];
			this.rhs.add(aSymbol);
		}
	}
	
	public NonTerminalSymbol getLhs() {
		return lhs;
	}

	public List<Symbol> getRhs() {
		return rhs;
	}

	

	
	 
}
 
