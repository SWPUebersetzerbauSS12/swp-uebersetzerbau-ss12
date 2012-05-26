package main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import main.model.Token;

public class GUI {
	
	Vector<String> tableColl;
	Vector<Vector<String>> tableRow;
	JTextArea codeArea;
	
	public GUI() {
		JFrame window = new JFrame("CodeGenerator Debugger");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new GridLayout(0,2));
		
		window.getContentPane().add(createTokenStreamTable());
		window.getContentPane().add(createCodeTextField());
		
		window.pack();
		window.setVisible(true);
	}
	
	private JPanel createCodeTextField() {
		JPanel codePanel = new JPanel();
		
		codeArea = new JTextArea(21,40);
		
		JScrollPane scrollPane = new JScrollPane(codeArea);
		codePanel.add(scrollPane);
		return codePanel;
	}

	private JPanel createTokenStreamTable() {
		JPanel tablePanel = new JPanel();
		
		tableColl = new Vector<String>();
		tableRow = new Vector<Vector<String>>();
		tableColl.add("#");
		tableColl.add("type");
		tableColl.add("target");
		tableColl.add("typeTarget");
		tableColl.add("op1");
		tableColl.add("typeOp1");
		tableColl.add("op2");
		tableColl.add("typeOp2");
		tableColl.add("parameter");
		
		DefaultTableModel tableModle = new DefaultTableModel(tableRow,tableColl);
		JTable table = new JTable(tableModle);
		table.setPreferredScrollableViewportSize(new Dimension(450, 300));
		table.setFillsViewportHeight(true);
		
		JScrollPane scrollPane = new JScrollPane(table);
		tablePanel.add(scrollPane);
		return tablePanel;
	}

	public void updateTokenStream(Token tok) {
		
		Vector<String> data = new Vector<String>();
		data.addElement(new String("" + tableRow.size()));
		data.addElement(tok.getType().toString());
		data.addElement(tok.getTarget());
		data.addElement(tok.getTypeTarget());
		data.addElement(tok.getOp1());
		data.addElement(tok.getTypeOp1());
		data.addElement(tok.getOp2());
		data.addElement(tok.getTypeOp2());
		data.addElement(new String("" + tok.getParameterCount()));
		
		tableRow.addElement(data);
	}
	
	public void updateCodeArea(String code) {
		codeArea.setText(code);
	}
}

