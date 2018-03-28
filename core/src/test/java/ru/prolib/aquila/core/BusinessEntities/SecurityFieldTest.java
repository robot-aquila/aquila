package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SecurityFieldTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testConstants() {
		assertEquals( 5, SecurityField.getVersion());
		assertEquals( 1, SecurityField.DISPLAY_NAME);
		assertEquals( 3, SecurityField.LOT_SIZE);
		assertEquals( 4, SecurityField.TICK_SIZE);
		assertEquals( 5, SecurityField.TICK_VALUE);
		assertEquals( 6, SecurityField.INITIAL_MARGIN);
		assertEquals( 7, SecurityField.SETTLEMENT_PRICE);
		assertEquals( 8, SecurityField.LOWER_PRICE_LIMIT);
		assertEquals( 9, SecurityField.UPPER_PRICE_LIMIT);
		assertEquals(10, SecurityField.OPEN_PRICE);
		assertEquals(11, SecurityField.HIGH_PRICE);
		assertEquals(12, SecurityField.LOW_PRICE);
		assertEquals(13, SecurityField.CLOSE_PRICE);
		assertEquals(14, SecurityField.EXPIRATION_TIME);
	}

}
