import static org.junit.Assert.assertEquals;

import org.junit.Test;

import analysis.EntryType;
import analysis.SymbolTable;
import analysis.ast.nodes.Id;
import analysis.ast.nodes.Type;

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
		table.insertEntry(new EntryType(new Id("a"), new Type()));
		table.insertEntry(new EntryType(new Id("a"), new Type()));
	}

}
