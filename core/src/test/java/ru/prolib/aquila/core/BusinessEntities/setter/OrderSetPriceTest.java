package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.setter.OrderSetPrice;

/**
 * 2012-09-26<br>
 * $Id: OrderSetPriceTest.java 298 2012-10-27 16:07:51Z whirlwind $
 */
public class OrderSetPriceTest {
	private static IMocksControl control;
	private static EditableOrder order;
	private static OrderSetPrice setter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		order = control.createMock(EditableOrder.class);
		setter = new OrderSetPrice();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ 123500L,				null,	false },
				{ new Integer(11),		null,	false },
				{ new Double(281.1D),	281.1D,	true  },
				{ null,					null,	false },
				{ new Boolean(false),	null,	false },
				{ this,					null,	false },
				{ new Float(763.44F),	null,	false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				order.setPrice((Double) fixture[i][1]);
			}
			control.replay();
			setter.set(order, fixture[i][0]);
			control.verify();
		}

	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new OrderSetPrice()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
