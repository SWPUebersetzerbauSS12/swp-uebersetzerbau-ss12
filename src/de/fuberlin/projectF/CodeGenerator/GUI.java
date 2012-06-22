package de.fuberlin.projectF.CodeGenerator;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import de.fuberlin.projectF.CodeGenerator.model.Token;

public class GUI {

	Vector<String> tableColl;
	Vector<Vector<String>> tableRow;
	JTextArea codeArea;

	public GUI() {
		JFrame window = new JFrame("CodeGenerator Debugger");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new GridLayout(0, 2));

		window.getContentPane().add(createTokenStreamTable());
		window.getContentPane().add(createCodeTextField());

		window.pack();
		window.setVisible(true);
	}

	private JPanel createCodeTextField() {
		JPanel codePanel = new JPanel();

		codeArea = new JTextArea(21, 40);

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

		DefaultTableModel tableModle = new DefaultTableModel(tableRow,
				tableColl);
		JTable table = new JTable(tableModle);
		table.setPreferredScrollableViewportSize(new Dimension(500, 300));
		table.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(table);
		tablePanel.add(scrollPane);
		return tablePanel;
	}

	public void updateTokenStreamTable(ArrayList<Token> code) {
		for (Token t : code) {
			Vector<String> data = new Vector<String>();
			data.addElement(new String("" + tableRow.size()));
			data.addElement(t.getType().toString());
			data.addElement(t.getTarget());
			data.addElement(t.getTypeTarget());
			data.addElement(t.getOp1());
			data.addElement(t.getTypeOp1());
			data.addElement(t.getOp2());
			data.addElement(t.getTypeOp2());
			data.addElement(new String("" + t.getParameterCount()));
			tableRow.addElement(data);
		}
	}

	public void updateCodeArea(String code) {
		codeArea.setText(code);
	}

	public void appendCodeArea(String text) {
		codeArea.append(text);
	}
}
