package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 2012-09-22<br>
 * $Id: EditableImplTest.java 280 2012-09-22 09:31:44Z whirlwind $
 */
public class EditableImplTest {
	public static final Integer MOD1 = 1;
	public static final Integer MOD2 = 2;
	private EditableImpl editable;

	@Before
	public void setUp() throws Exception {
		editable = new EditableImpl();
	}
	
	@Test
	public void testDefaults() throws Exception {
		assertFalse(editable.hasChanged());
		assertFalse(editable.hasChanged(MOD1));
		assertFalse(editable.hasChanged(MOD2));
		assertFalse(editable.isAvailable());
	}
	
	@Test
	public void testSetChanged0() throws Exception {
		editable.setChanged();
		assertTrue(editable.hasChanged());
		assertFalse(editable.hasChanged(MOD1));
		assertFalse(editable.hasChanged(MOD2));
		editable.resetChanges();
		assertFalse(editable.hasChanged());
		assertFalse(editable.hasChanged(MOD1));
		assertFalse(editable.hasChanged(MOD2));
	}
	
	@Test
	public void testSetChanged1() throws Exception {
		editable.setChanged(MOD1);
		assertTrue(editable.hasChanged());
		assertTrue(editable.hasChanged(MOD1));
		assertFalse(editable.hasChanged(MOD2));
		editable.resetChanges();
		assertFalse(editable.hasChanged());
		assertFalse(editable.hasChanged(MOD1));
		assertFalse(editable.hasChanged(MOD2));
	}
	
	@Test
	public void testSetAvailable() throws Exception {
		editable.setAvailable(true);
		assertTrue(editable.isAvailable());
		editable.setAvailable(false);
		assertFalse(editable.isAvailable());
	}

}
