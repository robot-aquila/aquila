package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.junit.Assert.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.BusinessEntities.setter.TradeSetQty;

/**
 * 2012-11-05<br>
 * $Id: TradeSetQtyTest.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class TradeSetQtyTest {
	private Trade trade;
	private TradeSetQty setter;

	@Before
	public void setUp() throws Exception {
		setter = new TradeSetQty();
	}

	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected, set?
				{ null,		null,	false },
				{ this,		null,	false },
				{ false,	null,	false },
				{ 71.123d,	71L,	true  },
				{ 123,		123L,	true  },
				{ 128L,		128L,	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			trade = new Trade(null);
			setter.set(trade, fixture[i][0]);
			if ( (Boolean) fixture[i][2] ) {
				assertEquals(fixture[i][1], trade.getQty());
			} else {
				assertNull(trade.getQty());
			}
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new TradeSetQty()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121105, 171123).toHashCode();
		assertEquals(hashCode, setter.hashCode());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("TradeSetQty", setter.toString());
	}
	
}
