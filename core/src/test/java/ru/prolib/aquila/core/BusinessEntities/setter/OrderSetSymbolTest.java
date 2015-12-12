package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.OrderSetSymbol;

public class OrderSetSymbolTest {
	private static IMocksControl control;
	private static EditableOrder order;
	private static OrderSetSymbol setter;
	private static Symbol symbol;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		order = control.createMock(EditableOrder.class);
		setter = new OrderSetSymbol();
		symbol = new Symbol("SBER", "EQBR", "RUB", SymbolType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ 100500L,				null,	false },
				{ symbol,				symbol,	true  },
				{ new Double(201.1D),	null,	false },
				{ null,					null,	false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				order.setSymbol((Symbol) fixture[i][1]);
			}
			control.replay();
			setter.set(order, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new OrderSetSymbol()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
