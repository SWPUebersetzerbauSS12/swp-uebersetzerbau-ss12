package de.fuberlin.projecta.parser;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum NonTerminal {
	program ("program"),
	funcs ("funcs"),
	func ("func"),
	func_ ("func'"),
	optparams ("optparams"),
	params ("params"),
	params_ ("params'"),
	block ("block"),
	decls ("decls"),
	decl ("decl"),
	type ("type"),
	type_ ("type'"),
	stmts ("stmts"),
	stmt ("stmt"),
	stmt_ ("stmt'"),
	stmt__ ("stmt''"),
	loc ("loc"),
	loc_ ("loc'"),
	loc__ ("loc''"),
	assign ("assign"),
	assign_ ("assign'"),
	bool ("bool"),
	bool_ ("bool'"),
	join ("join"),
	join_ ("join'"),
	equality ("equality"),
	equality_ ("equality'"),
	rel ("rel"),
	rel_ ("rel'"),
	expr ("expr"),
	expr_ ("expr'"),
	term ("term"),
	term_ ("term'"),
	unary ("unary"),
	factor ("factor"),
	factor_ ("factor'"),
	optargs ("optargs"),
	args ("args"),
	args_ ("args'");
	
	private static Map<String,NonTerminal> nonTerminalSymbol2NonTerminal=new HashMap<String, NonTerminal>();
	
	static{
		for(NonTerminal t : EnumSet.allOf(NonTerminal.class)){
			nonTerminalSymbol2NonTerminal.put(t.getNonTerminalSymbol(), t);
		}
	}
	
	private final String nonTerminalSymbol;
	
	private NonTerminal(String nonTerminalSymbol) {
		this.nonTerminalSymbol=nonTerminalSymbol;			
	}
	
	public String getNonTerminalSymbol(){
		return this.nonTerminalSymbol;
	}

	public static NonTerminal byNonTerminalSymbol(String s){
		return nonTerminalSymbol2NonTerminal.get(s);
	}
}