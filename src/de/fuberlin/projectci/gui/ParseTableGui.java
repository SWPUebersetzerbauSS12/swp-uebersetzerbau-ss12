package de.fuberlin.projectci.gui;

import java.awt.GridLayout;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.fuberlin.projectci.grammar.BNFGrammarReader;
import de.fuberlin.projectci.grammar.BNFParsingErrorException;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.GrammarReader;
import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;
import de.fuberlin.projectci.parseTable.Action;
import de.fuberlin.projectci.parseTable.ErrorAction;
import de.fuberlin.projectci.parseTable.Goto;
import de.fuberlin.projectci.parseTable.InvalidGrammarException;
import de.fuberlin.projectci.parseTable.ParseTable;
import de.fuberlin.projectci.parseTable.ParseTableBuilder;
import de.fuberlin.projectci.parseTable.SLRParseTableBuilder;
import de.fuberlin.projectci.parseTable.State;

/**
 * Swing-GUI zum Darstellen einer Parsetabelle mit ACTION- und GOTO-Funtktionen.
 *
 */
public class ParseTableGui {

	private Grammar g;
	private ParseTable pt;
	private Set<TerminalSymbol> terminals;
	private Set<NonTerminalSymbol> nonTerminals;
	private SortedSet<State> actionStates;
	private SortedSet<State> gotoStates;

	private static Comparator<State> stateComparator=new Comparator<State>() {
		
		@Override
		public int compare(State s1, State s2) {
			return s1.getId()-s2.getId();
		}
	};
	
	ParseTableGui() throws BNFParsingErrorException, InvalidGrammarException {
		GrammarReader gReader = new BNFGrammarReader();
		g = gReader.readGrammar("./input/de/fuberlin/projectci/quellsprache_bnf.txt");

		// Parsetabelle aufbauen
		ParseTableBuilder ptb = new SLRParseTableBuilder(g);
		pt = ptb.buildParseTable();

		// Symbole holen
		terminals = g.getAllTerminalSymols();
		terminals.add(Grammar.INPUT_ENDMARKER);
		nonTerminals = g.getAllNonTerminals();

		// States holen
		actionStates = new TreeSet<State>(stateComparator);
		actionStates.addAll(pt.state2ActionTable.keySet());
		
		gotoStates = new TreeSet<State>(stateComparator);
		gotoStates.addAll(pt.state2GotoTable.keySet());
	}

	public ParseTableGui(Grammar grammar, ParseTable parseTable){
		g=grammar;
		pt=parseTable;
		// Symbole holen
		terminals = grammar.getAllTerminalSymols();
		terminals.add(Grammar.INPUT_ENDMARKER);
		nonTerminals = grammar.getAllNonTerminals();

		// States holen
		actionStates = new TreeSet<State>(stateComparator);
		actionStates.addAll(parseTable.state2ActionTable.keySet());

		gotoStates = new TreeSet<State>(stateComparator);
		gotoStates.addAll(parseTable.state2GotoTable.keySet());
	}
	
	private DefaultTableModel getActionTableModel() {
		DefaultTableModel dtm = new DefaultTableModel();

		// Tabellenkopf initialisieren
		dtm.addColumn("State");

		for (TerminalSymbol t : terminals) {
			dtm.addColumn(t.getName());
		}

		// Tabelle Zeilenweise befüllen
		for (State s : actionStates) {
			Vector<String> row = new Vector<String>();
			row.add(String.valueOf(s.getId()));

			for (TerminalSymbol t : terminals) {
				// Strings verkürzen und Errors überspringen
				Action action = pt.getAction(s, t);

				if (action instanceof ErrorAction) {
					row.add("");
				} else {
					String actionStr = action.toString();
					actionStr = actionStr.replaceFirst("reduce", "R");
					actionStr = actionStr.replaceFirst("shift", "S");
					row.add(actionStr);
				}
			}

			dtm.addRow(row);

		}

		return dtm;

	}

	private DefaultTableModel getGotoTableModel() {
		DefaultTableModel dtm = new DefaultTableModel();

		// Tabellenkopf initialisieren
		dtm.addColumn("State");

		for (NonTerminalSymbol t : nonTerminals) {
			dtm.addColumn(t.getName());
		}
		
		// Tabelle Zeilenweise befüllen
		for (State s : gotoStates) {
			Vector<String> row = new Vector<String>();
			row.add(String.valueOf(s.getId()));

			for (NonTerminalSymbol nt : nonTerminals) {
				// Strings verarbeiten
				Goto go = pt.getGoto(s, nt);
				if (go == null)
					row.add("");
				else
					row.add(String.valueOf(go.getTargetState().getId()));
				
			}

			dtm.addRow(row);

		}
		
		
		return dtm;

	}

	public void showActionTable() {
		// Fenster erstellen
		JFrame frame = new JFrame("SLR Parsetabelle - ACTION");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Panel erstellen
		TablePanel panel = new TablePanel(getActionTableModel());
		frame.setContentPane(panel);

		// Fenster Anzeigen
		frame.pack();
		frame.setVisible(true);

		frame.pack();
	}

	public void showGotoTable() {
		// Fenster erstellen
		JFrame frame = new JFrame("SLR Parsetabelle - GOTO");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Panel erstellen
		TablePanel panel = new TablePanel(getGotoTableModel());
		frame.setContentPane(panel);

		// Fenster Anzeigen
		frame.pack();
		frame.setVisible(true);

		frame.pack();
	}

	public static void main(String[] args) throws BNFParsingErrorException,	InvalidGrammarException {
		ParseTableGui gui = new ParseTableGui();
		gui.showActionTable();
		gui.showGotoTable();

	}

	@SuppressWarnings("serial")
	public class TablePanel extends JPanel {


		private JTable table;

		public TablePanel(DefaultTableModel dtm) {
			// Fenster initialisieren
			super(new GridLayout(1, 0));

			// Tabelle initialisieren
			table = new JTable(dtm);

			JScrollPane scrollPane = new JScrollPane(table);
			add(scrollPane);

		}

	}

	
	
}
