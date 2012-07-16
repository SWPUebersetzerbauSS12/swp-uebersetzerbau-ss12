package de.fuberlin.projecta;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.parser.NonTerminal;
import de.fuberlin.projecta.parser.ParseTable;

public class ParseTableTest {
	
	private ParseTable table = new ParseTable();

	@Test
	public void testSetEntry() {
		table.setEntry(NonTerminal.program, TokenType.DEF, "program ::= funcs");
		String entry = table.getEntry(NonTerminal.program, TokenType.DEF);
		assertEquals("program ::= funcs", entry);
	}


	@Test(expected = IllegalStateException.class)
	public void testSetEntryTwice() {
		table.setEntry(NonTerminal.program, TokenType.DEF, "program ::= funcs");
		table.setEntry(NonTerminal.program, TokenType.DEF, "program ::= funcs");
	}

	@Test
	public void testGetInvalidEntry() {
		String entry = table.getEntry(NonTerminal.program, TokenType.DEF);
		assertEquals(null, entry);
	}

	@Test
	public void testGetEntries() {
		// funcs
		table.setEntry(NonTerminal.funcs, TokenType.DEF,
				"funcs ::=  func funcs");
		table.setEntry(NonTerminal.funcs, TokenType.EOF, "funcs ::= EPSILON");

		HashMap<TokenType,String> entries = table.getEntries(NonTerminal.funcs);
		assertEquals(2, entries.size());
		assertEquals("funcs ::=  func funcs", entries.get(TokenType.DEF));
		assertEquals("funcs ::= EPSILON", entries.get(TokenType.EOF));
	}

}
