package ru.prolib.aquila.quik.assembler.cache.dde;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.dde.utils.table.DDETableRange;
import ru.prolib.aquila.quik.assembler.cache.dde.TableMeta;

public class TableMetaTest {
	private TableMeta meta;
	
	@Test
	public void testHasHeaderRow() throws Exception {
		meta = new TableMeta(new DDETableRange(1, 1, 20, 10));
		assertTrue(meta.hasHeaderRow());
		meta = new TableMeta(new DDETableRange(2, 1, 20, 10));
		assertFalse(meta.hasHeaderRow());
	}
	
	@Test
	public void testHasDataRows() throws Exception {
		meta = new TableMeta(new DDETableRange(1, 1, 20, 10));
		assertTrue(meta.hasDataRows());
		meta = new TableMeta(new DDETableRange(1, 1, 1, 10));
		assertFalse(meta.hasDataRows());
		meta = new TableMeta(new DDETableRange(2, 1, 20, 10));
		assertTrue(meta.hasDataRows());
	}
	
	@Test
	public void testGetDataFirstRowNumber() throws Exception {
		meta = new TableMeta(new DDETableRange(1, 1, 20, 10));
		assertEquals(new Integer(2), meta.getDataFirstRowNumber());
		meta = new TableMeta(new DDETableRange(2, 1, 20, 10));
		assertEquals(new Integer(2), meta.getDataFirstRowNumber());
		meta = new TableMeta(new DDETableRange(10, 1, 10, 10));
		assertEquals(new Integer(10), meta.getDataFirstRowNumber());
		meta = new TableMeta(new DDETableRange(1, 1, 1, 10));
		assertNull(meta.getDataFirstRowNumber());
	}
	
	@Test
	public void testGetTableRange() throws Exception {
		DDETableRange range = new DDETableRange(1, 1, 20, 10);
		meta = new TableMeta(range);
		assertSame(range, meta.getTableRange());
		assertEquals(new DDETableRange(1, 1, 20, 10), meta.getTableRange());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		meta = new TableMeta(new DDETableRange(1, 1, 20, 10));
		assertTrue(meta.equals(meta));
		assertFalse(meta.equals(null));
		assertFalse(meta.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		meta = new TableMeta(new DDETableRange(1, 1, 20, 10));
		assertTrue(meta.equals(new TableMeta(new DDETableRange(1, 1, 20, 10))));
		assertFalse(meta.equals(new TableMeta(new DDETableRange(1, 1, 1, 10))));
	}

}
