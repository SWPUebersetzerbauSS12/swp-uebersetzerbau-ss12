package de.fuberlin.projecta;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.fuberlin.projecta.parser.Tree;

public class TreeTest {

	// helper
	static final class T extends Tree
	{
		public T() { super(null); }
	}

	@Test
	public void testTree() {
		T t1 = new T();
		T t2 = new T();

		t1.addChild(t2);
		assertEquals(1, t1.getChildrenCount());
		assertEquals(0, t2.getChildrenCount());
		assertEquals(null, t1.getParent());
		assertEquals(t1, t2.getParent());

		t1.removeChild(0);
		assertEquals(0, t1.getChildrenCount());
		assertEquals(0, t2.getChildrenCount());
		assertEquals(null, t1.getParent());
		assertEquals(null, t2.getParent());

		t2.setParent(t1);
		assertEquals(1, t1.getChildrenCount());
		assertEquals(0, t2.getChildrenCount());
		assertEquals(null, t1.getParent());
		assertEquals(t1, t2.getParent());

		t1.setParent(t2); // disallowed
		assertEquals(1, t1.getChildrenCount());
		assertEquals(0, t2.getChildrenCount());
		assertEquals(null, t1.getParent());
		assertEquals(t1, t2.getParent());

		t2.setParent(null);
		assertEquals(0, t1.getChildrenCount());
		assertEquals(null, t2.getParent());
	}

}
