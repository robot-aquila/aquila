package ru.prolib.aquila.ui.wrapper;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.G;

public class TableColumnTest {
	
	private static IMocksControl control;
	private G<?> getter;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		getter = control.createMock(G.class);
	}

	@Test
	public void testConstructor_WithTwoArgs() {
		TableColumn c = new TableColumn("COLUMN", getter);
		assertEquals("COLUMN", c.getName());
		assertEquals(getter, c.getGetter());
	}
	
	@Test
	public void testConstructor_WithThreeArgs() {
		TableColumn c = new TableColumn("COLUMN", getter, 250);
		assertEquals("COLUMN", c.getName());
		assertEquals(getter, c.getGetter());
		assertEquals(250, c.getWidth());
	}

}
