package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.junit.Assert.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.TradeSetDirection;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalDecorator;

/**
 * 2012-11-05<br>
 * $Id: TradeSetDirectionTest.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class TradeSetDirectionTest {
	private Trade trade;
	private TradeSetDirection setter;

	@Before
	public void setUp() throws Exception {
		setter = new TradeSetDirection();
	}

	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected, set?
				{ null,					null,				false },
				{ this,					null,				false },
				{ false,				null,				false },
				{ 71.123d,				null,				false },
				{ OrderDirection.BUY,	OrderDirection.BUY,	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			trade = new Trade(new TerminalDecorator());
			setter.set(trade, fixture[i][0]);
			if ( (Boolean) fixture[i][2] ) {
				assertSame(fixture[i][1], trade.getDirection());
			} else {
				assertNull(trade.getDirection());
			}
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new TradeSetDirection()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121105, 163921).toHashCode();
		assertEquals(hashCode, setter.hashCode());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("TradeSetDirection", setter.toString());
	}
	
}
