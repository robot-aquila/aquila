package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.junit.Assert.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.TradeSetSymbol;

public class TradeSetSymbolTest {
	private Symbol symbol;
	private Trade trade;
	private TradeSetSymbol setter;

	@Before
	public void setUp() throws Exception {
		setter = new TradeSetSymbol();
		symbol = new Symbol("AAPL","SMART","USD",SymbolType.STOCK);
	}

	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected, set?
				{ null,		null,		false },
				{ this,		null,		false },
				{ false,	null,		false },
				{ 71.123d,	null,		false },
				{ symbol,	symbol,	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			trade = new Trade(null);
			setter.set(trade, fixture[i][0]);
			if ( (Boolean) fixture[i][2] ) {
				assertSame(fixture[i][1], trade.getSymbol());
			} else {
				assertNull(trade.getSymbol());
			}
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new TradeSetSymbol()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121105, 162723).toHashCode();
		assertEquals(hashCode, setter.hashCode());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("TradeSetSymbol", setter.toString());
	}
	
}
