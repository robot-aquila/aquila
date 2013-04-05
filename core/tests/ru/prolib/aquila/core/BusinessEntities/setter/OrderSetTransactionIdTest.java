package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.setter.OrderSetTransactionId;

/**
 * 2012-09-26<br>
 * $Id: OrderSetTransactionIdTest.java 298 2012-10-27 16:07:51Z whirlwind $
 */
public class OrderSetTransactionIdTest {
	private static IMocksControl control;
	private static EditableOrder order;
	private static OrderSetTransactionId setter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		order = control.createMock(EditableOrder.class);
		setter = new OrderSetTransactionId();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ 100500L,				100500L, true  },
				{ new Integer(10),		10L,	 true  },
				{ new Double(201.1D),	201L,	 true  },
				{ null,					null,	 false },
				{ new Boolean(false),	null,	 false },
				{ this,					null,	 false },
				{ new Float(123.456),	123L,	 true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				order.setTransactionId((Long) fixture[i][1]);
			}
			control.replay();
			setter.set(order, fixture[i][0]);
			control.verify();
		}

	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new OrderSetTransactionId()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
