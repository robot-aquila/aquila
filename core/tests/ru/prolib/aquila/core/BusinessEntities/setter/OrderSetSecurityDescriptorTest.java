package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.OrderSetSecurityDescriptor;

/**
 * 2012-09-26<br>
 * $Id: OrderSetSecurityDescriptorTest.java 374 2012-12-25 02:22:40Z whirlwind $
 */
public class OrderSetSecurityDescriptorTest {
	private static IMocksControl control;
	private static EditableOrder order;
	private static OrderSetSecurityDescriptor setter;
	private static SecurityDescriptor descr;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		order = control.createMock(EditableOrder.class);
		setter = new OrderSetSecurityDescriptor();
		descr = new SecurityDescriptor("SBER", "EQBR", "RUR", SecurityType.STK);
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
				{ descr,				descr,	true  },
				{ new Double(201.1D),	null,	false },
				{ null,					null,	false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				order.setSecurityDescriptor((SecurityDescriptor) fixture[i][1]);
			}
			control.replay();
			setter.set(order, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new OrderSetSecurityDescriptor()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
