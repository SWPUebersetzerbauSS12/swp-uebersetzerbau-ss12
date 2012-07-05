package de.fuberlin.projecta;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SymbolTable;
import de.fuberlin.projecta.analysis.ast.nodes.BasicType;
import de.fuberlin.projecta.analysis.ast.nodes.Id;
import de.fuberlin.projecta.lexer.BasicTokenType;

public class SymbolTableTest {

	@Test
	public void testSymbolTable() {
		SymbolTable table = new SymbolTable();
		EntryType entry = table.lookup("a");
		assertEquals(entry, null);
	}

	@Test(expected = IllegalStateException.class)
	public void testDuplicateEntry() {
		SymbolTable table = new SymbolTable();
		table.insertEntry(new EntryType(new Id("a"), new BasicType(BasicTokenType.INT)));
		table.insertEntry(new EntryType(new Id("a"), new BasicType(BasicTokenType.INT)));
	}

}
