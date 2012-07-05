package de.fuberlin.projectci.test.driver;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.Production;
import de.fuberlin.projectci.grammar.Symbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;
import de.fuberlin.projectci.lrparser.SyntaxTreeNode;
import de.fuberlin.projectci.parseTable.AcceptAction;
import de.fuberlin.projectci.parseTable.Goto;
import de.fuberlin.projectci.parseTable.ParseTable;
import de.fuberlin.projectci.parseTable.ReduceAction;
import de.fuberlin.projectci.parseTable.ShiftAction;
import de.fuberlin.projectci.parseTable.State;
import de.fuberlin.projectci.test.driver.DriverTest.DriverTestDataProvider;

/** 
 * Testdaten anhand des Beispiels aus dem Drachenbuch (TODO Referenz angeben)
 *
 */
public class DriverTestDataProvider1 implements DriverTestDataProvider{	
	private ILexer lexer;
	private ParseTable parseTable;
	private Grammar grammar;

	public DriverTestDataProvider1() {
		super();
		setUp();
	}

	// **************************************************************************** 
	// * Implementierung von DriverTestDataProvider
	// ****************************************************************************

	@Override
	public ParseTable getParseTable() {
		return parseTable;
	}

	@Override
	public Grammar getGrammar() {
		return grammar;
	}

	@Override
	public ILexer getLexer() {
		return lexer;
	}
	
	@Override
	public ISyntaxTree expectedResult(){
		/*
		 *      Parsebaum               AST
		 *          E                    E
		 *      /   |   \            /   |   \
		 *      E   +   T           T    +   id
		 *      |       |        /  |  \
		 *      T       F        id *  id
		 *   /  |  \    |
		 *   T  *  F    id
		 *   |     |
		 *   F     id
		 *   |
		 *   id
		 */
		
		SyntaxTreeNode e=new SyntaxTreeNode(new NonTerminalSymbol("E")); 
		SyntaxTreeNode t=new SyntaxTreeNode(new NonTerminalSymbol("T")); 
		t.addChild(new SyntaxTreeNode(new TerminalSymbol("id")));
		t.addChild(new SyntaxTreeNode(new TerminalSymbol("*")));
		t.addChild(new SyntaxTreeNode(new TerminalSymbol("id")));
		e.addChild(t);
		e.addChild(new SyntaxTreeNode(new TerminalSymbol("+")));
		e.addChild(new SyntaxTreeNode(new TerminalSymbol("id")));
		
		return e;
	}
	
	// **************************************************************************** 
	// * Erstellung von parseTable, grammar und lexer
	// * TODO Generische Implementierung die den GrammarReader vewendet und die Parsetabelle aus TSV-Daten erstellt  
	// ****************************************************************************


	private void setUp(){
		parseTable=new ParseTable();

		State s0=new State(0);
		State s1=new State(1);
		State s2=new State(2);
		State s3=new State(3);
		State s4=new State(4);
		State s5=new State(5);
		State s6=new State(6);
		State s7=new State(7);
		State s8=new State(8);
		State s9=new State(9);
		State s10=new State(10);
		State s11=new State(11);

		TerminalSymbol ts0=new TerminalSymbol("id");
		TerminalSymbol ts1=new TerminalSymbol("+");
		TerminalSymbol ts2=new TerminalSymbol("*");		
		TerminalSymbol ts3=new TerminalSymbol("(");
		TerminalSymbol ts4=new TerminalSymbol(")");
		TerminalSymbol ts5=Grammar.INPUT_ENDMARKER;

		NonTerminalSymbol nts0=new NonTerminalSymbol("E");
		NonTerminalSymbol nts1=new NonTerminalSymbol("T");
		NonTerminalSymbol nts2=new NonTerminalSymbol("F");


		Production p1= new Production(nts0, new Symbol[]{nts0, ts1, nts1});
		Production p2= new Production(nts0, new Symbol[]{nts1});
		Production p3= new Production(nts1, new Symbol[]{nts1, ts2, nts2});
		Production p4= new Production(nts1, new Symbol[]{nts2});
		Production p5= new Production(nts2, new Symbol[]{ts3, nts0, ts4});
		Production p6= new Production(nts2, new Symbol[]{ts0});

		grammar=new Grammar();
		grammar.addProduction(p1);
		grammar.addProduction(p2);
		grammar.addProduction(p3);
		grammar.addProduction(p4);
		grammar.addProduction(p5);
		grammar.addProduction(p6);

		parseTable.setInitialState(s0);
		parseTable.getActionTableForState(s0).setActionForTerminalSymbol(new ShiftAction(s5), ts0);
		parseTable.getActionTableForState(s0).setActionForTerminalSymbol(new ShiftAction(s4), ts3);
		parseTable.getActionTableForState(s1).setActionForTerminalSymbol(new ShiftAction(s6), ts1);
		parseTable.getActionTableForState(s1).setActionForTerminalSymbol(new AcceptAction(), ts5);
		parseTable.getActionTableForState(s2).setActionForTerminalSymbol(new ReduceAction(p2), ts1);
		parseTable.getActionTableForState(s2).setActionForTerminalSymbol(new ShiftAction(s7), ts2);
		parseTable.getActionTableForState(s2).setActionForTerminalSymbol(new ReduceAction(p2), ts4);
		parseTable.getActionTableForState(s2).setActionForTerminalSymbol(new ReduceAction(p2), ts5);
		parseTable.getActionTableForState(s3).setActionForTerminalSymbol(new ReduceAction(p4), ts1);
		parseTable.getActionTableForState(s3).setActionForTerminalSymbol(new ReduceAction(p4), ts2);
		parseTable.getActionTableForState(s3).setActionForTerminalSymbol(new ReduceAction(p4), ts4);
		parseTable.getActionTableForState(s3).setActionForTerminalSymbol(new ReduceAction(p4), ts5);
		parseTable.getActionTableForState(s4).setActionForTerminalSymbol(new ShiftAction(s5), ts0);
		parseTable.getActionTableForState(s4).setActionForTerminalSymbol(new ShiftAction(s4), ts3);
		parseTable.getActionTableForState(s5).setActionForTerminalSymbol(new ReduceAction(p6), ts1);
		parseTable.getActionTableForState(s5).setActionForTerminalSymbol(new ReduceAction(p6), ts2);
		parseTable.getActionTableForState(s5).setActionForTerminalSymbol(new ReduceAction(p6), ts4);
		parseTable.getActionTableForState(s5).setActionForTerminalSymbol(new ReduceAction(p6), ts5);
		parseTable.getActionTableForState(s6).setActionForTerminalSymbol(new ShiftAction(s5), ts0);
		parseTable.getActionTableForState(s6).setActionForTerminalSymbol(new ShiftAction(s4), ts3);
		parseTable.getActionTableForState(s7).setActionForTerminalSymbol(new ShiftAction(s5), ts0);
		parseTable.getActionTableForState(s7).setActionForTerminalSymbol(new ShiftAction(s4), ts3);
		parseTable.getActionTableForState(s8).setActionForTerminalSymbol(new ShiftAction(s6), ts1);
		parseTable.getActionTableForState(s8).setActionForTerminalSymbol(new ShiftAction(s11), ts4);
		parseTable.getActionTableForState(s9).setActionForTerminalSymbol(new ReduceAction(p1), ts1);
		parseTable.getActionTableForState(s9).setActionForTerminalSymbol(new ShiftAction(s7), ts2);
		parseTable.getActionTableForState(s9).setActionForTerminalSymbol(new ReduceAction(p1), ts4);
		parseTable.getActionTableForState(s9).setActionForTerminalSymbol(new ReduceAction(p1), ts5);
		parseTable.getActionTableForState(s10).setActionForTerminalSymbol(new ReduceAction(p3), ts1);
		parseTable.getActionTableForState(s10).setActionForTerminalSymbol(new ReduceAction(p3), ts2);
		parseTable.getActionTableForState(s10).setActionForTerminalSymbol(new ReduceAction(p3), ts4);
		parseTable.getActionTableForState(s10).setActionForTerminalSymbol(new ReduceAction(p3), ts5);
		parseTable.getActionTableForState(s11).setActionForTerminalSymbol(new ReduceAction(p5), ts1);
		parseTable.getActionTableForState(s11).setActionForTerminalSymbol(new ReduceAction(p5), ts2);
		parseTable.getActionTableForState(s11).setActionForTerminalSymbol(new ReduceAction(p5), ts4);
		parseTable.getActionTableForState(s11).setActionForTerminalSymbol(new ReduceAction(p5), ts5);

		parseTable.getGotoTableForState(s0).setGotoForNonTerminalSymbol(new Goto(s1), nts0);
		parseTable.getGotoTableForState(s0).setGotoForNonTerminalSymbol(new Goto(s2), nts1);
		parseTable.getGotoTableForState(s0).setGotoForNonTerminalSymbol(new Goto(s3), nts2);		
		parseTable.getGotoTableForState(s4).setGotoForNonTerminalSymbol(new Goto(s8), nts0);
		parseTable.getGotoTableForState(s4).setGotoForNonTerminalSymbol(new Goto(s2), nts1);
		parseTable.getGotoTableForState(s4).setGotoForNonTerminalSymbol(new Goto(s3), nts2);		
		parseTable.getGotoTableForState(s6).setGotoForNonTerminalSymbol(new Goto(s9), nts1);
		parseTable.getGotoTableForState(s6).setGotoForNonTerminalSymbol(new Goto(s3), nts2);
		parseTable.getGotoTableForState(s7).setGotoForNonTerminalSymbol(new Goto(s10), nts2);

		lexer=new DummyLexer();
		((DummyLexer)lexer).addToken(TokenType.ID, null);
		((DummyLexer)lexer).addToken(TokenType.OP_MUL, null);
		((DummyLexer)lexer).addToken(TokenType.ID, null);
		((DummyLexer)lexer).addToken(TokenType.OP_ADD, null);
		((DummyLexer)lexer).addToken(TokenType.ID, null);
		((DummyLexer)lexer).addToken(TokenType.EOF, null);
	}

}
