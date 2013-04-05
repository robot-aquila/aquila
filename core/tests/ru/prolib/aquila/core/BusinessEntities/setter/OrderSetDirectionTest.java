package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.setter.OrderSetDirection;

/**
 * 2012-09-26<br>
 * $Id: OrderSetDirectionTest.java 298 2012-10-27 16:07:51Z whirlwind $
 */
public class OrderSetDirectionTest {
	private static IMocksControl control;
	private static EditableOrder order;
	private static OrderSetDirection setter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		order = control.createMock(EditableOrder.class);
		setter = new OrderSetDirection();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ 100500L,				null,					false },
				{ OrderDirection.SELL,  OrderDirection.SELL,	true  },
				{ new Double(201.1D),	null,					false },
				{ null,					null,					false },
				{ OrderDirection.BUY,	OrderDirection.BUY,		true  },
				{ this,					null,					false },
				{ new Float(123.456),	null,					false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				order.setDirection((OrderDirection) fixture[i][1]);
			}
			control.replay();
			setter.set(order, fixture[i][0]);
			control.verify();
		}

	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new OrderSetDirection()));
		assertFalse(setter.equals(this));
		assertFalse(setter.equals(null));
	}

}
