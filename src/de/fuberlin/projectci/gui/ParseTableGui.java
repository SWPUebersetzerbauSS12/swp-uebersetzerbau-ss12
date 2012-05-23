package de.fuberlin.projectci.gui;

import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.fuberlin.projectci.grammar.BNFParsingErrorException;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.GrammarReader;
import de.fuberlin.projectci.grammar.TerminalSymbol;
import de.fuberlin.projectci.parseTable.Action;
import de.fuberlin.projectci.parseTable.ErrorAction;
import de.fuberlin.projectci.parseTable.InvalidGrammarException;
import de.fuberlin.projectci.parseTable.ParseTable;
import de.fuberlin.projectci.parseTable.ParseTableBuilder;
import de.fuberlin.projectci.parseTable.SLRParseTableBuilder;
import de.fuberlin.projectci.parseTable.State;

public class ParseTableGui extends JPanel {

	/**
	 * @param args
	 */
	
	private JTable table;
	private DefaultTableModel dtm;
	
	
	ParseTableGui() {
		// Fenster initialisieren
		 super(new GridLayout(1,0));
		

		// Tabelle initialisieren
		dtm = new DefaultTableModel();	
		table = new JTable(dtm);

        JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
		

	}
	
	public void showParseTable() throws BNFParsingErrorException, InvalidGrammarException {
		Grammar g = GrammarReader.readGrammar("./doc/quellsprache_bnf.txt");
		
		// Symbole holen
		Set<TerminalSymbol> terminals = g.getAllTerminalSymols();
		
		
		//Parsetabelle aufbauen
		ParseTableBuilder ptb = new SLRParseTableBuilder(g);
		ParseTable pt = ptb.buildParseTable();
	
		
		// Tabellenkopf initialisieren
		dtm.addColumn("State");
		
		Set<State> states = new HashSet<State>();
		states.addAll(pt.state2ActionTable.keySet());		
		
		for(TerminalSymbol t : terminals) {
			dtm.addColumn(t.getName());
		}
		
		// Tabelle Zeilenweise befüllen
		for(State s : states) {
			Vector<String> row = new Vector<String>();
			row.add(s.getName());
			
			for(TerminalSymbol t : terminals) {
				// Strings verkürzen und Errors überspringen
				Action action = pt.getAction(s, t);
				
				if(action instanceof ErrorAction) {
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
		
		

	}
	
	public static void main(String[] args) throws BNFParsingErrorException, InvalidGrammarException {
		
		
		
		// Fenster erstellen
		JFrame frame = new JFrame("SLR Parsetabelle");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Panel erstellen
		ParseTableGui panel = new ParseTableGui();
		frame.setContentPane(panel);
		
		// Fenster Anzeigen
		frame.pack();
		frame.setVisible(true);
		
		panel.showParseTable();
		frame.pack();
		

	}

}
