package ru.prolib.aquila.util;


import static org.junit.Assert.*;
import org.junit.*;

/**
 * 2012-05-14<br>
 * $Id: FixedListTest.java 216 2012-05-14 16:13:13Z whirlwind $
 */
public class FixedListTest {
	private FixedList<Integer> list;

	@Before
	public void setUp() throws Exception {
		list = new FixedList<Integer>(3);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals(3, list.getMaximalCapacity());
	}
	
	@Test
	public void testAddLast() throws Exception {
		list.addLast(1);
		list.addLast(2);
		list.addLast(3);
		list.addLast(4);
		assertEquals(3, list.size());
		assertEquals((Integer)2, list.get(0));
		assertEquals((Integer)3, list.get(1));
		assertEquals((Integer)4, list.get(2));
		list.addLast(5);
		assertEquals(3, list.size());
		assertEquals((Integer)3, list.get(0));
		assertEquals((Integer)4, list.get(1));
		assertEquals((Integer)5, list.get(2));
	}

}
