package ru.prolib.aquila.dde.utils.table;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class DDETableRangeTest {
	private DDETableRange range;

	@Before
	public void setUp() throws Exception {
		range = new DDETableRange(1, 5, 10, 6);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(range.equals(range));
		assertFalse(range.equals(this));
		assertFalse(range.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Integer> vFirstRow = new Variant<Integer>()
			.add(1)
			.add(298);
		Variant<Integer> vFirstCol = new Variant<Integer>(vFirstRow)
			.add(5)
			.add(30);
		Variant<Integer> vLastRow = new Variant<Integer>(vFirstCol)
			.add(10)
			.add(756);
		Variant<Integer> vLastCol = new Variant<Integer>(vLastRow)
			.add(6)
			.add(912);
		Variant<?> iterator = vLastCol;
		int foundCnt = 0;
		DDETableRange x = null, found = null;
		do {
			x = new DDETableRange(vFirstRow.get(), vFirstCol.get(),
					vLastRow.get(), vLastCol.get());
			if ( range.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(1, found.getFirstRow());
		assertEquals(5, found.getFirstCol());
		assertEquals(10, found.getLastRow());
		assertEquals(6, found.getLastCol());
	}

}
