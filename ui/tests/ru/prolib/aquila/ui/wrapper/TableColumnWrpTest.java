package ru.prolib.aquila.ui.wrapper;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import javax.swing.table.TableColumn;

import org.easymock.IMocksControl;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.G;

public class TableColumnWrpTest {
	
	private static IMocksControl control;
	private G<?> getter;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		getter = control.createMock(G.class);
	}

	@Test
	public void testConstructor_Basic() {
		TableColumnWrp c = new TableColumnWrp("COLUMN", getter);
		assertEquals("COLUMN", c.getName());
		assertEquals(getter, c.getGetter());
	}
	
	@Test
	public void testConstructor_WithWidth() {
		TableColumnWrp c = new TableColumnWrp("COLUMN", getter, 250);
		assertEquals("COLUMN", c.getName());
		assertEquals(getter, c.getGetter());
		assertEquals(250, c.getWidth());
	}
	
	@Test
	public void testConstructor_WithText() {
		TableColumnWrp c = new TableColumnWrp("COLUMN", getter, "Something");
		assertEquals("COLUMN", c.getName());
		assertEquals(getter, c.getGetter());
		assertEquals("Something", c.getText());
	}
	
	@Test
	public void testConstructor_WithTextAndWidth() {
		TableColumnWrp c = new TableColumnWrp("COLUMN", getter, "Something", 250);
		assertEquals("COLUMN", c.getName());
		assertEquals(getter, c.getGetter());
		assertEquals("Something", c.getText());
		assertEquals(250, c.getWidth());
	}
	
	@Test
	public void testSetUnderlayed() {
		TableColumnWrp c = new TableColumnWrp("COLUMN", getter);
		TableColumn underlayed = new TableColumn();
		c.setUnderlayed(underlayed);
		assertEquals(underlayed, c.getUnderlayed());
	}
	
	@Test
	public void testStart_WidthNotSet() throws Exception {
		TableColumnWrp c = new TableColumnWrp("COLUMN", getter);
		TableColumn underlayed = new TableColumn();
		c.setUnderlayed(underlayed);
		c.start();
		
		assertEquals(15, underlayed.getPreferredWidth());
	}
	
	@Test
	public void testStart_WidthIsSet() throws Exception {
		TableColumnWrp c = new TableColumnWrp("COLUMN", getter, 250);
		TableColumn underlayed = new TableColumn();
		c.setUnderlayed(underlayed);
		c.start();
		
		assertEquals(250, underlayed.getPreferredWidth());
	}


}
