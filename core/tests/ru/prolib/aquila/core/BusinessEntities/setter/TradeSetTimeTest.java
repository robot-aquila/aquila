package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.junit.Assert.*;

import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.TradeSetTime;

/**
 * 2012-11-05<br>
 * $Id: TradeSetTimeTest.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class TradeSetTimeTest {
	private Trade trade;
	private TradeSetTime setter;

	@Before
	public void setUp() throws Exception {
		setter = new TradeSetTime();
	}

	@Test
	public void testSet() throws Exception {
		Date time = new Date();
		Object fixture[][] = {
				// value, expected, set?
				{ null,		null,	false },
				{ this,		null,	false },
				{ false,	null,	false },
				{ 71.123d,	null,	false },
				{ time,		time,	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			trade = new Trade(null);
			setter.set(trade, fixture[i][0]);
			if ( (Boolean) fixture[i][2] ) {
				assertSame(fixture[i][1], trade.getTime());
			} else {
				assertNull(trade.getTime());
			}
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new TradeSetTime()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121105, 164505).toHashCode();
		assertEquals(hashCode, setter.hashCode());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("TradeSetTime", setter.toString());
	}
	
}
