import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import analysis.EntryType;
import analysis.SymbolTable;
import analysis.SymbolTableStack;
import analysis.ast.nodes.Id;
import analysis.ast.nodes.Type;

public class SymbolTableStackTest {

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

	@Test
	public void testSymbolTableStack() {
		SymbolTableStack stack = new SymbolTableStack();
		stack.push();
		assertEquals(1, stack.size());

		stack.top().insertEntry(new EntryType(new Id("a"), new Type()));

		stack.push();
		assertEquals(stack.size(), 2);

		EntryType entry = stack.findEntry("a");
		assertTrue(entry != null);
	}

	@Test
	public void testSymbolTableStack1() {
		SymbolTableStack stack = new SymbolTableStack();
		stack.push();
		stack.top().insertEntry(new EntryType(new Id("a"), new Type()));
		stack.push();
		assertTrue(stack.findEntry("a") != null);
		stack.pop();
		assertTrue(stack.findEntry("a") != null);
		stack.pop();
		assertTrue(stack.findEntry("a") == null);
	}

}
