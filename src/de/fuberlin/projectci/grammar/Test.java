package de.fuberlin.projectci.grammar;

import java.util.LinkedList;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NonTerminalSymbol S = new NonTerminalSymbol("S");
		NonTerminalSymbol A = new NonTerminalSymbol("A");
		
		TerminalSymbol b = new TerminalSymbol("b");
		TerminalSymbol c = new TerminalSymbol("c");
		
		LinkedList<Symbol> l = new LinkedList<Symbol>();
		l.add(A);
		l.add(b);
		l.add(c);
		
		Production p = new Production(S, l);
		Production q = new Production(A,new Symbol[] {b});
		
		Grammar g = new Grammar();
		g.addProduction(p);
		g.addProduction(q);
		
		System.out.println(g);

	}

}
