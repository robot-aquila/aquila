package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.setter.OrderSetQty;

/**
 * 2012-09-26<br>
 * $Id: OrderSetQtyTest.java 298 2012-10-27 16:07:51Z whirlwind $
 */
public class OrderSetQtyTest {
	private static IMocksControl control;
	private static EditableOrder order;
	private static OrderSetQty setter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		order = control.createMock(EditableOrder.class);
		setter = new OrderSetQty();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ 123500L,				123500L, true  },
				{ new Integer(11),		11L,	 true  },
				{ new Double(281.1D),	281L,	 true  },
				{ null,					null,	 false },
				{ new Boolean(false),	null,	 false },
				{ this,					null,	 false },
				{ new Float(763.456),	763L,	 true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				order.setQty((Long) fixture[i][1]);
			}
			control.replay();
			setter.set(order, fixture[i][0]);
			control.verify();
		}

	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new OrderSetQty()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
