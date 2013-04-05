package ru.prolib.aquila.ui.wrapper;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;

import org.easymock.IMocksControl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.G;
/**
 * $Id: TableModelTest.java 575 2013-03-13 23:40:00Z huan.kaktus $
 */
public class TableModelTest {
	
	private static IMocksControl control;
	private G<?> getter;
	private TableModel tm;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		getter = control.createMock(G.class);
		tm = new TableModel();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddColum_Ok() throws Exception {
		TableColumn col = new TableColumn("MY_COL", getter);
		tm.addColumn(col);
		assertTrue(tm.isColumnExists("MY_COL"));
		assertEquals(col, tm.getColumn("MY_COL"));
		
		G<?> getter2 = control.createMock(G.class);
		tm.addColumn(new TableColumn("COL_2", getter2));
		List<String> index = tm.getIndex();
		assertEquals(2, index.size());
		assertEquals("MY_COL", index.get(0));
		assertEquals("COL_2", index.get(1));
		
		assertEquals(getter, tm.getGetters().get("MY_COL"));
		assertEquals(getter2, tm.getGetters().get("COL_2"));
	}
	
	@Test(expected=TableColumnAlreadyExistsException.class)
	public void testAddColumn_AlreadyExistsThrows() throws Exception {
		TableColumn col = new TableColumn("MY_COL", getter);
		tm.addColumn(col);
		tm.addColumn(col);
	}
	
	@Test(expected=TableColumnNotExistsException.class)
	public void testGetColumn_NotExistsThrows() throws Exception {
		tm.getColumn("MY_COL");
	}

}
