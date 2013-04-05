package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.TradeSetVolume;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalDecorator;

/**
 * 2012-11-05<br>
 * $Id: TradeSetVolumeTest.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class TradeSetVolumeTest {
	private Trade trade;
	private TradeSetVolume setter;

	@Before
	public void setUp() throws Exception {
		setter = new TradeSetVolume();
	}

	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected, set?
				{ null,		null,		false },
				{ this,		null,		false },
				{ false,	null,		false },
				{ 71.123d,	71.123d,	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			trade = new Trade(new TerminalDecorator());
			setter.set(trade, fixture[i][0]);
			if ( (Boolean) fixture[i][2] ) {
				assertEquals(fixture[i][1], trade.getVolume());
			} else {
				assertNull(trade.getVolume());
			}
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new TradeSetVolume()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121105, 165517).toHashCode();
		assertEquals(hashCode, setter.hashCode());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("TradeSetVolume", setter.toString());
	}
	
}
