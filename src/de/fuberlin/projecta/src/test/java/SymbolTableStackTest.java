import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import semantic.analysis.SymbolTable;
import semantic.analysis.SymbolTableStack;

public class SymbolTableStackTest {

	@Test
	public void testSymbolTable() {
		SymbolTable table = new SymbolTable();
		Object object = table.lookup("foo");
		assertEquals(object, null);

		table.insertEntry("foo", true);
	}

	@Test(expected = IllegalStateException.class)
	public void testDuplicateEntry() {
		SymbolTable table = new SymbolTable();
		table.insertEntry("foo", true);
		table.insertEntry("foo", true);
	}

	@Test
	public void testSymbolTableStack() {
		SymbolTableStack stack = new SymbolTableStack();
		assertEquals(stack.size(), 1);

		stack.top().insertEntry("a", true);

		stack.push();
		assertEquals(stack.size(), 2);

		Object object = stack.findEntry("a");
		assertTrue(object != null);
	}
	
	@Test
	public void testSymbolTableStack1() {
		SymbolTableStack stack = new SymbolTableStack();
		stack.push();
		stack.top().insertEntry("a", true);
		stack.push();
		assertTrue(stack.findEntry("a") != null);
		stack.pop();
		assertTrue(stack.findEntry("a") != null);
		stack.pop();
		assertTrue(stack.findEntry("a") == null);
	}

}
