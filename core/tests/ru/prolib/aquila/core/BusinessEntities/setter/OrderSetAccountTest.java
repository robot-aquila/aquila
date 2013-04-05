package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.setter.OrderSetAccount;

/**
 * 2012-09-26<br>
 * $Id: OrderSetAccountTest.java 298 2012-10-27 16:07:51Z whirlwind $
 */
public class OrderSetAccountTest {
	private static IMocksControl control;
	private static EditableOrder order;
	private static OrderSetAccount setter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		order = control.createMock(EditableOrder.class);
		setter = new OrderSetAccount();
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
				{ new Account("LX001"), new Account("LX001"),	true  },
				{ new Double(201.1D),	null,					false },
				{ null,					null,					false },
				{ this,					null,					false },
				{ new Float(123.456),	null,					false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				order.setAccount((Account) fixture[i][1]);
			}
			control.replay();
			setter.set(order, fixture[i][0]);
			control.verify();
		}

	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new OrderSetAccount()));
		assertFalse(setter.equals(this));
		assertFalse(setter.equals(null));
	}

}
